package com.epharmacy.utils;

import com.epharmacy.ui.AppIcons;
import com.epharmacy.ui.UiComponents;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/** Common setup for feature screens (header, sidebar brand, back nav, tables). */
public final class ScreenUi {

    private ScreenUi() {}

    public static void setup(Parent root, StackPane brandMark, Button backButton,
                             String headerTitle, String breadcrumb) {
        HeaderBarHelper.configure(root, headerTitle, breadcrumb, false);
        if (brandMark != null) {
            DashboardUi.styleSidebarBrand(brandMark);
            brandMark.getChildren().setAll(AppIcons.brand(FontAwesomeSolid.PILLS));
        }
        if (backButton != null) {
            NavStyler.sidebar(backButton, FontAwesomeSolid.ARROW_LEFT);
        }
    }

    public static void table(TableView<?> table, String emptyTitle, String emptySubtitle) {
        UiComponents.bindTablePlaceholder(table, emptyTitle, emptySubtitle);
    }

    public static void actionButton(Button button, FontAwesomeSolid icon) {
        NavStyler.action(button, icon);
    }
}
