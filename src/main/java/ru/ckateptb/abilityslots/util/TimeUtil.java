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