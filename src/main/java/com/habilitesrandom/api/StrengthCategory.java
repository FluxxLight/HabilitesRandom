package com.habilitesrandom.api;

/** Future strong/weak classification. UNKNOWN keeps this Prompt's empty profiles honest. */
public enum StrengthCategory {
    UNKNOWN("unknown"),
    STRONG("strong"),
    WEAK("weak");

    private final String serializedName;

    StrengthCategory(String serializedName) {
        this.serializedName = serializedName;
    }

    public String serializedName() {
        return serializedName;
    }

    public static StrengthCategory fromString(String value) {
        if (value == null || value.isBlank()) {
            return UNKNOWN;
        }
        for (StrengthCategory category : values()) {
            if (category.serializedName.equalsIgnoreCase(value) || category.name().equalsIgnoreCase(value)) {
                return category;
            }
        }
        return UNKNOWN;
    }
}
