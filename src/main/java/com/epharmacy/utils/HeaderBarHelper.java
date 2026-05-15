package com.epharmacy.utils;

import com.epharmacy.controllers.HeaderBarController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.Optional;

/**
 * Configures the shared {@code HeaderBar.fxml} included in dashboard screens.
 */
public final class HeaderBarHelper {

    private HeaderBarHelper() {}

    public static void configure(Parent root, String pageTitle, String breadcrumb, boolean showSearch) {
        Runnable task = () -> findController(root).ifPresent(ctrl -> {
            ctrl.setPageTitle(pageTitle);
            ctrl.setBreadcrumb(breadcrumb);
            ctrl.setSearchVisible(showSearch);
        });
        Platform.runLater(task);
    }

    public static void configure(Parent root, String pageTitle) {
        configure(root, pageTitle, null, false);
    }

    private static Optional<HeaderBarController> findController(Parent root) {
        if (root == null) return Optional.empty();
        Object direct = root.getProperties().get("headerBarController");
        if (direct instanceof HeaderBarController hbc) {
            return Optional.of(hbc);
        }
        for (Node child : root.getChildrenUnmodifiable()) {
            Object onChild = child.getProperties().get("headerBarController");
            if (onChild instanceof HeaderBarController hbc) {
                return Optional.of(hbc);
            }
            if (child instanceof Parent parent) {
                Optional<HeaderBarController> nested = findController(parent);
                if (nested.isPresent()) return nested;
            }
        }
        return Optional.empty();
    }
}
