/*
 * Copyright (c) 2022 CKATEPTb <https://github.com/CKATEPTb>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.ckateptb.abilityslots.service;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import ru.ckateptb.abilityslots.AbilitySlots;
import ru.ckateptb.abilityslots.ability.Ability;
import ru.ckateptb.abilityslots.ability.enums.ActivationMethod;
import ru.ckateptb.abilityslots.ability.info.AbilityInfo;
import ru.ckateptb.abilityslots.ability.info.AnnotationBasedAbilityInformation;
import ru.ckateptb.abilityslots.ability.sequence.Sequence;
import ru.ckateptb.abilityslots.category.AbilityCategory;
import ru.ckateptb.abilityslots.util.ClassPath;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AddonService {
    private final AbilitySlots plugin = AbilitySlots.getInstance();
    private final List<Class<?>> loadedClasses = new ArrayList<>();
    private final AbilityCategoryService abilityCategoryService;
    private final AbilitySequenceService abilitySequenceService;
    private final AbilityService abilityService;
    private final String nameRegex = "[a-zA-Z]+";

    public AddonService(AbilityCategoryService abilityCategoryService, AbilitySequenceService abilitySequenceService, AbilityService abilityService) {
        this.abilityCategoryService = abilityCategoryService;
        this.abilitySequenceService = abilitySequenceService;
        this.abilityService = abilityService;
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public void loadAddons() {
        loadedClasses.clear();
        File abilities = Paths.get(plugin.getDataFolder().getPath(), "abilities").toFile();
        FileUtils.forceMkdir(abilities);
        List<File> files = getFilesFromFolder(abilities);
        Map<JarFile, URL> jarFileURLMap = new HashMap<>();
        for (File file : files) {
            try {
                new JarInputStream(new FileInputStream(file));
            } catch (Exception e) {
                // not a jar file
                continue;
            }
            jarFileURLMap.put(new JarFile(file), file.toURI().toURL());
        }
        ClassLoader classLoader = URLClassLoader.newInstance(jarFileURLMap.values().toArray(new URL[0]), plugin.getClass().getClassLoader());
        for (JarFile file : jarFileURLMap.keySet()) {
            loadedClasses.addAll(
                    ClassPath.from(classLoader, file).getAllClasses().stream()
                            .map(ClassPath.ClassInfo::load)
                            .filter(cl -> !cl.isInterface() && !Modifier.isAbstract(cl.getModifiers()))
                            .collect(Collectors.toList())
            );
        }
        if (loadedClasses.isEmpty()) return;
        loadedClasses.forEach(cl -> {
            if (ClassUtils.isAssignable(AbilityCategory.class, cl)) {
                List<Constructor<?>> constructors = Arrays.asList(cl.getConstructors());
                constructors.removeIf(constructor -> constructor.getParameterCount() > 0);
                if (constructors.isEmpty()) {
                    log.warn("Found a new category for abilities ({}), but the developer made a mistake and did not add an empty constructor, please pass this information to him", cl.getName());
                } else {
                    AbilityCategory abilityCategory = null;
                    try {
                        abilityCategory = (AbilityCategory) constructors.get(0).newInstance();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        log.error("Failed to register category " + cl.getName(), e);
                    }
                    if (abilityCategory != null) {
                        String categoryName = abilityCategory.getName();
                        if (categoryName.matches(nameRegex)) {
                            log.info("Found a new category for abilities ({})", categoryName);
                            abilityCategoryService.registerCategory(abilityCategory);
                        } else {
                            log.info("Found a new category for abilities ({}), but the developer made a mistake. The name must contain characters from a-zA-Z", categoryName);
                        }
                    }
                }
            }
        });
        loadedClasses.forEach(cl -> {
            if (ClassUtils.isAssignable(Ability.class, cl)) {
                if (AnnotatedElementUtils.isAnnotated(cl, AbilityInfo.class)) {
                    List<Constructor<?>> constructors = Arrays.asList(cl.getConstructors());
                    constructors.removeIf(constructor -> constructor.getParameterCount() > 0);
                    if (constructors.isEmpty()) {
                        log.warn("Found a new ability ({}), but the developer made a mistake and did not add an empty constructor, please pass this information to him", cl.getName());
                    } else {
                        AbilityInfo abilityInfo = cl.getAnnotation(AbilityInfo.class);
                        if (abilityInfo.activationMethods().length == 0) {
                            log.warn("Found a new ability ({}), but the developer made a mistake and did not add any ActivationMethod in AbilityInfo annotation, please pass this information to him", cl.getName());
                        } else {
                            String categoryName = abilityInfo.category();
                            AbilityCategory category = abilityCategoryService.getCategory(categoryName);
                            if (category == null) {
                                log.warn("Found a new ability ({}), but the category ({}) specified by the developer does not exist, please pass this information to him", cl.getName(), categoryName);
                            } else {
                                if (abilityInfo.name().matches(nameRegex)) {
                                    AnnotationBasedAbilityInformation ability = new AnnotationBasedAbilityInformation(abilityInfo, category, (Class<? extends Ability>) cl);
                                    log.info("Found a new ability ({})", ability.getName());
                                    category.registerAbility(ability);
                                    abilityService.registerAbility(ability);
                                    if (ability.isActivatedBy(ActivationMethod.SEQUENCE)) {
                                        if (!AnnotatedElementUtils.isAnnotated(cl, Sequence.class)) {
                                            log.warn("Ability ({}) activate by SEQUENCE, but the developer made a mistake and did not add Sequence annotation, please pass this information to him", ability.getName());
                                        } else {
                                            abilitySequenceService.registerSequence(ability, cl.getAnnotation(Sequence.class));
                                        }
                                    }
                                } else {
                                    log.info("Found a new ability ({}), but the developer made a mistake. The name must contain characters from a-zA-Z", categoryName);
                                }
                            }
                        }
                    }
                } else {
                    log.warn("A new ability was found ({}), but the developer did not add the AbilityInfo annotation to it, please pass this information to him", cl.getName());
                }
            }
        });
    }

    public List<File> getFilesFromFolder(File folder) {
        List<File> files = new ArrayList<>();
        File[] filesInFolder = folder.listFiles();
        if (filesInFolder != null) {
            for (final File fileEntry : filesInFolder) {
                if (fileEntry.isDirectory()) {
                    files.addAll(getFilesFromFolder(fileEntry));
                } else {
                    files.add(fileEntry);
                }
            }
        }
        return files;
    }
}
