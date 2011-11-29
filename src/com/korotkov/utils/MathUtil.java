package com.korotkov.utils;

public class MathUtil {
    private MathUtil() {
    }

    public static int signum(double a) {
        if (a > 0.0) {
            return 1;
        }
        if (a < 0.0) {
            return -1;
        }
        return 0;
    }
}
