package com.epharmacy.utils;

import com.epharmacy.models.User;
import com.epharmacy.patterns.singleton.SessionManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/** Cached navigation with theme support and page fade transitions. */
public class SceneNavigator {

    private static final Map<String, CachedView> CACHE = new ConcurrentHashMap<>();
    private static Scene sharedScene;

    private static final List<String> STYLESHEETS = List.of(
            resource("/css/app-base.css"),
            resource("/css/themes.css"),
            resource("/css/components.css")
    );

    private record CachedView(Parent root, Object controller) {}

    private static String resource(String path) {
        return Objects.requireNonNull(SceneNavigator.class.getResource(path)).toExternalForm();
    }

    public static void navigateTo(Stage stage, String fxmlPath) throws IOException {
        if (fxmlPath.contains("Login.fxml")) {
            clearCache();
        }

        CachedView view = CACHE.computeIfAbsent(fxmlPath, SceneNavigator::loadView);
        Parent nextRoot = view.root();

        if (!nextRoot.getStyleClass().contains("app-root")) {
            nextRoot.getStyleClass().add("app-root");
        }

        if (sharedScene == null) {
            sharedScene = new Scene(nextRoot);
            applyStylesheets(sharedScene);
            ThemeManager.applyTo(sharedScene);
            fadeIn(nextRoot);
        } else {
            Parent current = sharedScene.getRoot();
            if (current == nextRoot) {
                ThemeManager.applyTo(sharedScene);
                stage.setScene(sharedScene);
                stage.show();
                refreshIfNeeded(view);
                return;
            }
            fadeSwap(current, nextRoot, () -> {
                sharedScene.setRoot(nextRoot);
                ThemeManager.applyTo(sharedScene);
                fadeIn(nextRoot);
            });
        }

        stage.setScene(sharedScene);
        stage.show();
        refreshIfNeeded(view);
    }

    public static void navigateToDashboard(Stage stage) throws IOException {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) {
            navigateTo(stage, "/fxml/Login.fxml");
            return;
        }

        String fxml = switch (user.getRole()) {
            case ADMIN      -> "/fxml/AdminDashboard.fxml";
            case PHARMACIST -> "/fxml/PharmacistDashboard.fxml";
            case PATIENT    -> "/fxml/PatientDashboard.fxml";
        };
        navigateTo(stage, fxml);
    }

    public static Scene getSharedScene() {
        return sharedScene;
    }

    public static void clearCache() {
        CACHE.clear();
        sharedScene = null;
    }

    private static void applyStylesheets(Scene scene) {
        scene.getStylesheets().clear();
        for (String sheet : STYLESHEETS) {
            if (!scene.getStylesheets().contains(sheet)) {
                scene.getStylesheets().add(sheet);
            }
        }
    }

    private static void fadeSwap(Parent from, Parent to, Runnable onSwapped) {
        // Swap immediately to avoid white background flash
        onSwapped.run();
    }

    private static void fadeIn(Parent root) {
        root.setOpacity(0.8);
        FadeTransition in = new FadeTransition(Duration.millis(150), root);
        in.setFromValue(0.8);
        in.setToValue(1.0);
        in.play();
    }

    private static void refreshIfNeeded(CachedView view) {
        if (view.controller() instanceof Refreshable r) {
            r.refresh();
        }
    }

    private static CachedView loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    Objects.requireNonNull(SceneNavigator.class.getResource(fxmlPath)));
            Parent root = loader.load();
            return new CachedView(root, loader.getController());
        } catch (Exception e) {
            try {
                java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter("error_log.txt"));
                e.printStackTrace(pw);
                pw.close();
            } catch (Exception ignored) {}
            String cause = e.getCause() != null ? e.getCause().toString() : e.toString();
            throw new RuntimeException("Failed to load " + fxmlPath + "\nCause: " + cause + "\n(See error_log.txt for full stack trace)", e);
        }
    }
}
