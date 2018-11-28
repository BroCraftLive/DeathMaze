package com.georlegacy.general.deathmaze.util;

public class FormatCheckUtil {

    public static FormatCheckResult checkFormat(Class returnType, String toFormat) {
        if (toFormat == null) {
            return FormatCheckResult.NULL;
        }
        if (returnType.equals(String.class)) {
            return FormatCheckResult.STRING;
        }
        if (returnType.equals(Boolean.class)) {
            try {
                boolean b = Boolean.parseBoolean(toFormat);
            } catch (IllegalArgumentException e) {
                return FormatCheckResult.FAIL;
            }
            return FormatCheckResult.BOOLEAN;
        }
        if (returnType.equals(Integer.class)) {
            try {
                int i = Integer.parseInt(toFormat);
            } catch (NumberFormatException e) {
                return FormatCheckResult.FAIL;
            }
            return FormatCheckResult.INT;
        }
        return FormatCheckResult.INCOMPATIBLE_TYPE;
    }

    public enum FormatCheckResult {
        NULL,
        STRING,
        BOOLEAN,
        INT,
        INCOMPATIBLE_TYPE,
        FAIL;
    }

}
