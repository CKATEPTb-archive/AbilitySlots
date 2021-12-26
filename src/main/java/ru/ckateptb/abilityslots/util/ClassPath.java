package ru.ckateptb.abilityslots.util;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Scans the source of a {@link ClassLoader} and finds all loadable classes and resources.
 *
 * <p><b>Warning:</b> Currently only {@link URLClassLoader} and only {@code file://} urls are
 * supported.
 *
 * @author Ben Yu
 * @since 14.0
 */
public final class ClassPath {
    private static final Logger logger = Logger.getLogger(ClassPath.class.getName());

    /**
     * Separator for the Class-Path manifest attribute value in jar files.
     */
    private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();

    private static final String CLASS_FILE_NAME_EXTENSION = ".class";

    private final ImmutableSet<ResourceInfo> resources;

    private ClassPath(ImmutableSet<ResourceInfo> resources) {
        this.resources = resources;
    }

    public static ClassPath from(ClassLoader classloader, JarFile jarFile) {
        DefaultScanner scanner = new DefaultScanner();
        scanner.scanJarFile(classloader, jarFile);
        return new ClassPath(scanner.getResources());
    }

    @VisibleForTesting
    static String getClassName(String filename) {
        int classNameEnd = filename.length() - CLASS_FILE_NAME_EXTENSION.length();
        return filename.substring(0, classNameEnd).replace('/', '.');
    }

    /**
     * Returns all classes loadable from the current class path.
     *
     * @since 16.0
     */
    public Set<ClassInfo> getAllClasses() {
        return resources.stream().filter(ClassInfo.class::isInstance).map(ClassInfo.class::cast).collect(Collectors.toSet());
    }

    /**
     * Represents a class path resource that can be either a class file or any other resource file
     * loadable from the class path.
     *
     * @since 14.0
     */
    @Beta
    public static class ResourceInfo {
        final ClassLoader loader;
        private final String resourceName;

        ResourceInfo(String resourceName, ClassLoader loader) {
            this.resourceName = checkNotNull(resourceName);
            this.loader = checkNotNull(loader);
        }

        static ResourceInfo of(String resourceName, ClassLoader loader) {
            if (resourceName.endsWith(CLASS_FILE_NAME_EXTENSION)) {
                return new ClassInfo(resourceName, loader);
            } else {
                return new ResourceInfo(resourceName, loader);
            }
        }

        @Override
        public int hashCode() {
            return resourceName.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ResourceInfo that) {
                return resourceName.equals(that.resourceName) && loader == that.loader;
            }
            return false;
        }

        // Do not change this arbitrarily. We rely on it for sorting ResourceInfo.
        @Override
        public String toString() {
            return resourceName;
        }
    }

    /**
     * Represents a class that can be loaded through {@link #load}.
     *
     * @since 14.0
     */
    @Beta
    public static final class ClassInfo extends ResourceInfo {
        private final String className;

        ClassInfo(String resourceName, ClassLoader loader) {
            super(resourceName, loader);
            this.className = getClassName(resourceName);
        }

        /**
         * Returns the fully qualified name of the class.
         *
         * <p>Behaves identically to {@link Class#getName()} but does not require the class to be
         * loaded.
         */
        public String getName() {
            return className;
        }

        /**
         * Loads (but doesn't link or initialize) the class.
         *
         * @throws LinkageError when there were errors in loading classes that this class depends on.
         *                      For example, {@link NoClassDefFoundError}.
         */
        public Class<?> load() {
            try {
                return loader.loadClass(className);
            } catch (ClassNotFoundException e) {
                // Shouldn't happen, since the class name is read from the class path.
                throw new IllegalStateException(e);
            }
        }

        @Override
        public String toString() {
            return className;
        }
    }

    /**
     * Abstract class that scans through the class path represented by a {@link ClassLoader} and calls
     * {@link #scanDirectory} and {@link #scanJarFile} for directories and jar files on the class path
     * respectively.
     */
    abstract static class Scanner {

        // We only scan each file once independent of the classloader that resource might be associated
        // with.
        private final Set<File> scannedUris = Sets.newHashSet();

        /**
         * Returns the class path URIs specified by the {@code Class-Path} manifest attribute, according
         * to
         * <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html#Main_Attributes">
         * JAR File Specification</a>. If {@code manifest} is null, it means the jar file has no
         * manifest, and an empty set will be returned.
         */
        @VisibleForTesting
        static ImmutableSet<File> getClassPathFromManifest(File jarFile, @Nullable Manifest manifest) {
            if (manifest == null) {
                return ImmutableSet.of();
            }
            ImmutableSet.Builder<File> builder = ImmutableSet.builder();
            String classpathAttribute =
                    manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH.toString());
            if (classpathAttribute != null) {
                for (String path : CLASS_PATH_ATTRIBUTE_SEPARATOR.split(classpathAttribute)) {
                    URL url;
                    try {
                        url = getClassPathEntry(jarFile, path);
                    } catch (MalformedURLException e) {
                        // Ignore bad entry
                        logger.warning("Invalid Class-Path entry: " + path);
                        continue;
                    }
                    if (url.getProtocol().equals("file")) {
                        builder.add(new File(url.getFile()));
                    }
                }
            }
            return builder.build();
        }

        /**
         * Returns the absolute uri of the Class-Path entry value as specified in
         * <a href="http://docs.oracle.com/javase/8/docs/technotes/guides/jar/jar.html#Main_Attributes">
         * JAR File Specification</a>. Even though the specification only talks about relative urls,
         * absolute urls are actually supported too (for example, in Maven surefire plugin).
         */
        @VisibleForTesting
        static URL getClassPathEntry(File jarFile, String path) throws MalformedURLException {
            return new URL(jarFile.toURI().toURL(), path);
        }

        /**
         * Called when a directory is scanned for resource files.
         */
        protected abstract void scanDirectory(ClassLoader loader, File directory) throws IOException;

        /**
         * Called when a jar file is scanned for resource entries.
         */
        protected abstract void scanJarFile(ClassLoader loader, JarFile file) throws IOException;

        @VisibleForTesting
        final void scan(File file, ClassLoader classloader) throws IOException {
            if (scannedUris.add(file.getCanonicalFile())) {
                scanFrom(file, classloader);
            }
        }

        private void scanFrom(File file, ClassLoader classloader) throws IOException {
            try {
                if (!file.exists()) {
                    return;
                }
            } catch (SecurityException e) {
                logger.warning("Cannot access " + file + ": " + e);
                return;
            }
            if (file.isDirectory()) {
                scanDirectory(classloader, file);
            } else {
                scanJar(file, classloader);
            }
        }

        private void scanJar(File file, ClassLoader classloader) throws IOException {
            JarFile jarFile;
            try {
                jarFile = new JarFile(file);
            } catch (IOException e) {
                // Not a jar file
                return;
            }
            try {
                for (File path : getClassPathFromManifest(file, jarFile.getManifest())) {
                    scan(path, classloader);
                }
                scanJarFile(classloader, jarFile);
            } finally {
                try {
                    jarFile.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @VisibleForTesting
    static final class DefaultScanner extends Scanner {
        private final SetMultimap<ClassLoader, String> resources = MultimapBuilder.hashKeys().linkedHashSetValues().build();

        ImmutableSet<ResourceInfo> getResources() {
            ImmutableSet.Builder<ResourceInfo> builder = ImmutableSet.builder();
            for (Map.Entry<ClassLoader, String> entry : resources.entries()) {
                builder.add(ResourceInfo.of(entry.getValue(), entry.getKey()));
            }
            return builder.build();
        }

        @Override
        protected void scanJarFile(ClassLoader classloader, JarFile file) {
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory() || entry.getName().equals(JarFile.MANIFEST_NAME)) {
                    continue;
                }
                resources.get(classloader).add(entry.getName());
            }
        }

        @Override
        protected void scanDirectory(ClassLoader classloader, File directory) {
            scanDirectory(directory, classloader, "");
        }

        private void scanDirectory(File directory, ClassLoader classloader, String packagePrefix) {
            File[] files = directory.listFiles();
            if (files == null) {
                logger.warning("Cannot read directory " + directory);
                // IO error, just skip the directory
                return;
            }
            for (File f : files) {
                String name = f.getName();
                if (f.isDirectory()) {
                    scanDirectory(f, classloader, packagePrefix + name + "/");
                } else {
                    String resourceName = packagePrefix + name;
                    if (!resourceName.equals(JarFile.MANIFEST_NAME)) {
                        resources.get(classloader).add(resourceName);
                    }
                }
            }
        }
    }
}
