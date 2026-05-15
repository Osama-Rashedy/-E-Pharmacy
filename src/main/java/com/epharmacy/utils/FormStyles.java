package com.epharmacy.utils;

import javafx.scene.control.Control;

/** Validation visuals for form fields. */
public final class FormStyles {

    private FormStyles() {}

    public static void setError(Control field, boolean error) {
        if (field == null) return;
        if (error) {
            if (!field.getStyleClass().contains("field-error")) {
                field.getStyleClass().add("field-error");
            }
        } else {
            field.getStyleClass().remove("field-error");
        }
    }

    public static void clearErrors(Control... fields) {
        if (fields == null) return;
        for (Control c : fields) {
            setError(c, false);
        }
    }
}
