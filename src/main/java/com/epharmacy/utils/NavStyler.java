package com.epharmacy.utils;

import com.epharmacy.ui.AppIcons;
import javafx.scene.control.Button;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/** Applies consistent sidebar / action icons to FXML-defined buttons. */
public final class NavStyler {

    private NavStyler() {}

    public static void sidebar(Button button, FontAwesomeSolid icon) {
        if (button == null) return;
        button.setGraphic(AppIcons.nav(icon));
        button.setGraphicTextGap(12);
    }

    public static void action(Button button, FontAwesomeSolid icon) {
        if (button == null) return;
        button.setGraphic(AppIcons.action(icon));
        button.setGraphicTextGap(8);
    }
}
