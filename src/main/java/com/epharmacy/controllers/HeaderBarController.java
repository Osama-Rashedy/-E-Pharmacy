package com.epharmacy.controllers;

import com.epharmacy.ui.AppIcons;
import com.epharmacy.utils.ThemeManager;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

/** Sticky top bar: branding, optional search, animated theme toggle. */
public class HeaderBarController {

    private static final double THUMB_TRAVEL = 36;

    @FXML private HBox brandRow;
    @FXML private StackPane brandIconHost;
    @FXML private Label titleLabel;
    @FXML private Label breadcrumbLabel;
    @FXML private TextField globalSearch;
    @FXML private StackPane themeToggleTrack;
    @FXML private Region themeThumb;
    @FXML private Label sunIcon;
    @FXML private Label moonIcon;

    private final java.util.function.Consumer<ThemeManager.Mode> themeListener =
            m -> Platform.runLater(() -> syncThumb(true));

    @FXML
    private void initialize() {
        ThemeManager.addListener(themeListener);
        syncThumb(false);
        setBrandIcon(AppIcons.brand(FontAwesomeSolid.PILLS));
        registerController();
    }

    private void registerController() {
        Platform.runLater(() -> {
            Node node = brandRow != null ? brandRow : titleLabel;
            while (node != null) {
                node.getProperties().put("headerBarController", this);
                if (node.getStyleClass().contains("app-header")) {
                    break;
                }
                node = node.getParent();
            }
        });
    }

    public void setPageTitle(String title) {
        if (titleLabel != null && title != null) {
            titleLabel.setText(title);
        }
    }

    public void setBreadcrumb(String crumb) {
        if (breadcrumbLabel == null) return;
        if (crumb == null || crumb.isBlank()) {
            breadcrumbLabel.setVisible(false);
            breadcrumbLabel.setManaged(false);
        } else {
            breadcrumbLabel.setText(crumb);
            breadcrumbLabel.setVisible(true);
            breadcrumbLabel.setManaged(true);
        }
    }

    public void setBrandIcon(FontIcon icon) {
        if (brandIconHost == null || icon == null) return;
        brandIconHost.getChildren().setAll(icon);
    }

    public void setSearchVisible(boolean visible) {
        if (globalSearch != null) {
            globalSearch.setVisible(visible);
            globalSearch.setManaged(visible);
        }
    }

    public TextField getSearchField() {
        return globalSearch;
    }

    @FXML
    private void onThemeToggle(MouseEvent event) {
        if (themeToggleTrack == null || themeToggleTrack.getScene() == null) return;
        ThemeManager.toggle(themeToggleTrack.getScene());
    }

    private void syncThumb(boolean animate) {
        if (themeThumb == null) return;
        double target = ThemeManager.isDark() ? THUMB_TRAVEL : 0;
        updateIconStates();
        if (!animate) {
            themeThumb.setTranslateX(target);
            return;
        }
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), themeThumb);
        tt.setToX(target);
        tt.play();
    }

    private void updateIconStates() {
        if (sunIcon == null || moonIcon == null) return;
        sunIcon.getStyleClass().removeAll("theme-toggle-icon-active", "theme-toggle-icon-idle");
        moonIcon.getStyleClass().removeAll("theme-toggle-icon-active", "theme-toggle-icon-idle");
        if (ThemeManager.isDark()) {
            moonIcon.getStyleClass().add("theme-toggle-icon-active");
            sunIcon.getStyleClass().add("theme-toggle-icon-idle");
        } else {
            sunIcon.getStyleClass().add("theme-toggle-icon-active");
            moonIcon.getStyleClass().add("theme-toggle-icon-idle");
        }
    }

    public void dispose() {
        ThemeManager.removeListener(themeListener);
    }
}
