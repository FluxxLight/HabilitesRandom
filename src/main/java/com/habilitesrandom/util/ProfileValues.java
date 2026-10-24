package com.habilitesrandom.util;

import net.minecraft.resources.Identifier;

/** Shared validation and wire-format helpers. */
public final class ProfileValues {
    public static final String NONE = "";
    public static final int MAX_TEXT_LENGTH = 128;
    public static final int MAX_ABILITIES = 4;

    private ProfileValues() {
    }

    public static String clean(String value) {
        if (value == null) {
            return NONE;
        }
        return value.length() <= MAX_TEXT_LENGTH ? value : value.substring(0, MAX_TEXT_LENGTH);
    }

    public static String cleanId(String value) {
        String clean = clean(value);
        if (clean.isEmpty() || clean.length() > MAX_TEXT_LENGTH) {
            return NONE;
        }
        return Identifier.tryParse(clean) == null ? NONE : clean;
    }

    public static String nullableId(Identifier id) {
        return id == null ? NONE : cleanId(id.toString());
    }

    public static Identifier parseId(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return Identifier.tryParse(value);
    }

    public static int nonNegative(int value) {
        return Math.max(0, value);
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static String joinCooldowns(int[] values) {
        if (values == null || values.length == 0) {
            return "0,0,0,0";
        }
        StringBuilder builder = new StringBuilder(24);
        for (int index = 0; index < MAX_ABILITIES; index++) {
            if (index > 0) {
                builder.append(',');
            }
            int value = index < values.length ? Math.max(0, values[index]) : 0;
            builder.append(value);
        }
        return builder.toString();
    }

    public static int[] parseCooldowns(String csv) {
        int[] values = new int[MAX_ABILITIES];
        if (csv == null || csv.isBlank()) {
            return values;
        }
        String[] parts = csv.split(",", MAX_ABILITIES + 1);
        for (int index = 0; index < MAX_ABILITIES && index < parts.length; index++) {
            try {
                values[index] = Math.max(0, Integer.parseInt(parts[index].trim()));
            } catch (NumberFormatException ignored) {
                values[index] = 0;
            }
        }
        return values;
    }
}
