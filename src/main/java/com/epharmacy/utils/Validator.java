package com.epharmacy.utils;

import javafx.scene.control.TextField;

/** Validation helpers for form fields. */
public class Validator {

    public static boolean isEmailValid(String email) {
        return email != null && email.matches("^[\\w.+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isNotEmpty(String value) {
        return value != null && !value.isBlank();
    }

    public static boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isPositiveNumber(String value) {
        try { return Double.parseDouble(value) > 0; }
        catch (NumberFormatException e) { return false; }
    }

    public static boolean isNonNegativeInt(String value) {
        try { return Integer.parseInt(value) >= 0; }
        catch (NumberFormatException e) { return false; }
    }

    /** Highlights a field red if invalid, clears style if valid. */
    public static void markValid(TextField field, boolean valid) {
        if (valid) {
            field.getStyleClass().remove("field-error");
        } else if (!field.getStyleClass().contains("field-error")) {
            field.getStyleClass().add("field-error");
        }
    }
}
