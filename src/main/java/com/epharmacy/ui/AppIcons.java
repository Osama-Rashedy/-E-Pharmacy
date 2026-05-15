package com.epharmacy.ui;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Central Font Awesome (Ikonli) icons for consistent navigation and actions.
 */
public final class AppIcons {

    public static final int NAV = 16;
    public static final int ACTION = 14;
    public static final int STAT = 20;
    public static final int BRAND = 28;

    private AppIcons() {}

    public static FontIcon nav(FontAwesomeSolid icon) {
        return icon(icon, "nav-icon", NAV);
    }

    public static FontIcon action(FontAwesomeSolid icon) {
        return icon(icon, "action-icon", ACTION);
    }

    public static FontIcon stat(FontAwesomeSolid icon, String colorStyle) {
        FontIcon fi = icon(icon, "stat-card-icon", STAT);
        if (colorStyle != null && !colorStyle.isBlank()) {
            fi.getStyleClass().add(colorStyle);
        }
        return fi;
    }

    public static FontIcon brand(FontAwesomeSolid icon) {
        return icon(icon, "brand-icon", BRAND);
    }

    public static FontIcon icon(FontAwesomeSolid glyph, String styleClass, int size) {
        FontIcon fi = new FontIcon(glyph);
        fi.setIconSize(size);
        if (styleClass != null && !styleClass.isBlank()) {
            fi.getStyleClass().add(styleClass);
        }
        return fi;
    }
}
