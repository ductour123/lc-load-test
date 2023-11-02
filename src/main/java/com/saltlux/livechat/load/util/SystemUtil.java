package com.saltlux.livechat.load.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class SystemUtil {
    public static void sleep(TimeUnit timeUnit, long duration) {
        try {
            timeUnit.sleep(duration);
        } catch (Exception e) {}
    }

    public static int getRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static String toHumanReadableTimeFormat(long ms) {
        Duration duration = Duration.of(ms, ChronoUnit.MILLIS);
        return duration.toString().substring(2).replaceAll("(\\d[HMS])(?!$)", "$1 ").toLowerCase();
    }

    public static double calculatePercentage(long obtained, long total) {
        if(obtained == 0 || total == 0)
            return 0d;
        return Math.round(((double) obtained / total) * 1000.0) / 10.0;
    }

}
