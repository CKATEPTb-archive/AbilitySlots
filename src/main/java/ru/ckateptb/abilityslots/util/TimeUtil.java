package ru.ckateptb.abilityslots.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {
    public static String formatTime(long time) {
        String sign = "";
        if (time < 0L) {
            sign = "-";
            time = Math.abs(time);
        }

        long days = time / TimeUnit.DAYS.toMillis(1L);
        long hours = time % TimeUnit.DAYS.toMillis(1L) / TimeUnit.HOURS.toMillis(1L);
        long minutes = time % TimeUnit.HOURS.toMillis(1L) / TimeUnit.MINUTES.toMillis(1L);
        long seconds = time % TimeUnit.MINUTES.toMillis(1L) / TimeUnit.SECONDS.toMillis(1L);
        String formatted = sign;
        if (days > 0L) {
            formatted = sign + days + "d ";
        }

        if (hours > 0L) {
            formatted = formatted + hours + "h ";
        }

        if (minutes > 0L) {
            formatted = formatted + minutes + "m ";
        }

        if (seconds >= 0L) {
            formatted = formatted + seconds + "s";
        }

        return formatted;
    }
}