package com.epharmacy.utils;

import com.epharmacy.ui.UiAnimations;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.util.ArrayList;
import java.util.List;

/** Shared dashboard shell setup: header, sidebar icons, entrance animations. */
public final class DashboardUi {

    private DashboardUi() {}

    public static void initShell(Parent root, String title, String breadcrumb) {
        HeaderBarHelper.configure(root, title, breadcrumb, false);
    }

    public static void styleSidebarBrand(Region brandMark) {
        if (brandMark != null && !brandMark.getStyleClass().contains("sidebar-brand-mark")) {
            brandMark.getStyleClass().add("sidebar-brand-mark");
        }
    }

    public static void wireSidebar(Button... items) {
        for (Button b : items) {
            if (b == null) continue;
            b.setOnMouseEntered(e -> UiAnimations.hoverLift(b, true));
            b.setOnMouseExited(e -> UiAnimations.hoverLift(b, false));
        }
    }

    public static void animateContent(VBox contentArea, Node... statCards) {
        Platform.runLater(() -> {
            if (contentArea != null) {
                UiAnimations.slideUpFadeIn(contentArea);
            }
            if (statCards != null && statCards.length > 0) {
                List<Node> cards = new ArrayList<>();
                for (Node n : statCards) {
                    if (n != null) cards.add(n);
                }
                UiAnimations.staggerFadeIn(cards);
            }
        });
    }

    public record NavItem(Button button, FontAwesomeSolid icon) {}

    public static void applyNavIcons(NavItem... items) {
        if (items == null) return;
        for (NavItem item : items) {
            if (item.button() != null && item.icon() != null) {
                NavStyler.sidebar(item.button(), item.icon());
            }
        }
    }
}
