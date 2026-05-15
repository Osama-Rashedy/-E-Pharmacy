package com.epharmacy.utils;

import javafx.animation.FadeTransition;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.prefs.Preferences;

/**
 * Light/dark theme state. Persists preference via {@link Preferences}
 * (desktop equivalent of localStorage). Applies {@code theme-light} /
 * {@code theme-dark} on the scene root for CSS token switching.
 */
public final class ThemeManager {

    public enum Mode { LIGHT, DARK }

    private static final Preferences PREFS =
            Preferences.userNodeForPackage(ThemeManager.class);
    private static final List<Consumer<Mode>> LISTENERS = new CopyOnWriteArrayList<>();

    private static Mode mode = Mode.LIGHT;

    private ThemeManager() {}

    public static void init() {
        String saved = PREFS.get("theme", "light");
        mode = "dark".equalsIgnoreCase(saved) ? Mode.DARK : Mode.LIGHT;
    }

    public static Mode getMode() {
        return mode;
    }

    public static boolean isDark() {
        return mode == Mode.DARK;
    }

    public static void addListener(Consumer<Mode> listener) {
        LISTENERS.add(Objects.requireNonNull(listener));
    }

    public static void removeListener(Consumer<Mode> listener) {
        LISTENERS.remove(listener);
    }

    public static void applyTo(Parent root) {
        if (root == null) return;
        root.getStyleClass().removeAll("theme-light", "theme-dark");
        root.getStyleClass().add(mode == Mode.LIGHT ? "theme-light" : "theme-dark");
        if (!root.getStyleClass().contains("app-root")) {
            root.getStyleClass().add("app-root");
        }
    }

    public static void applyTo(Scene scene) {
        if (scene != null) {
            applyTo(scene.getRoot());
        }
    }

    public static void toggle(Scene scene) {
        if (scene == null) return;
        setMode(scene, mode == Mode.LIGHT ? Mode.DARK : Mode.LIGHT, true);
    }

    public static void setMode(Scene scene, Mode newMode, boolean animate) {
        if (scene == null || newMode == null || newMode == mode) return;

        Parent root = scene.getRoot();
        if (!animate) {
            mode = newMode;
            persist();
            applyTo(root);
            notifyListeners();
            return;
        }

        FadeTransition out = new FadeTransition(Duration.millis(150), root);
        out.setFromValue(1.0);
        out.setToValue(0.88);
        out.setOnFinished(e -> {
            mode = newMode;
            persist();
            applyTo(root);
            notifyListeners();

            FadeTransition in = new FadeTransition(Duration.millis(150), root);
            in.setFromValue(0.88);
            in.setToValue(1.0);
            in.play();
        });
        out.play();
    }

    private static void persist() {
        PREFS.put("theme", mode.name().toLowerCase());
    }

    private static void notifyListeners() {
        for (Consumer<Mode> listener : LISTENERS) {
            listener.accept(mode);
        }
    }
}
