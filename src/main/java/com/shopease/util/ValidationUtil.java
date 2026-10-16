package com.shopease.util;

import java.util.regex.Pattern;

public final class ValidationUtil {
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^[6-9]\\d{9}$");
    private static final Pattern PINCODE = Pattern.compile("^\\d{6}$");

    private ValidationUtil() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static boolean isEmail(String value) {
        return !isBlank(value) && EMAIL.matcher(value.trim()).matches();
    }

    public static boolean isPhone(String value) {
        return !isBlank(value) && PHONE.matcher(value.trim()).matches();
    }

    public static boolean isPincode(String value) {
        return !isBlank(value) && PINCODE.matcher(value.trim()).matches();
    }

    public static Integer parsePositiveInt(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            int parsed = Integer.parseInt(value.trim());
            return parsed > 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double parseNonNegativeDouble(String value) {
        if (isBlank(value)) {
            return null;
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed >= 0 ? parsed : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
