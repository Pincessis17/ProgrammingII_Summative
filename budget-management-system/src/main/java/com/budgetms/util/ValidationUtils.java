package com.budgetms.util;

/**
 * Small, UI-independent validation helpers used by the JavaFX controllers.
 * Kept separate from the controllers (which need a running JavaFX toolkit)
 * so this logic can be unit tested directly with JUnit 5.
 */
public final class ValidationUtils {

    private ValidationUtils() {
    }

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
    }

    public static double requirePositiveNumber(String value, String fieldName) {
        double parsed;
        try {
            parsed = Double.parseDouble(value);
        } catch (NumberFormatException | NullPointerException e) {
            throw new IllegalArgumentException(fieldName + " must be a number.");
        }

        if (parsed < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative.");
        }

        return parsed;
    }
}
