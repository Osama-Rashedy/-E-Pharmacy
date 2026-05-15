package com.epharmacy;

import com.epharmacy.database.DatabaseInitializer;
import com.epharmacy.utils.FontLoader;
import com.epharmacy.utils.ThemeManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Application entry point.
 * Initialises the database on first launch, then loads the Login screen.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FontLoader.loadApplicationFonts();
        ThemeManager.init();

        DatabaseInitializer.initialize();

        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/Login.fxml")));
        Parent root = loader.load();
        root.getStyleClass().add("app-root");

        Scene scene = new Scene(root, 1100, 720);
        ThemeManager.applyTo(scene);

        scene.getStylesheets().addAll(
                Objects.requireNonNull(getClass().getResource("/css/app-base.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/themes.css")).toExternalForm(),
                Objects.requireNonNull(getClass().getResource("/css/components.css")).toExternalForm());

        primaryStage.setTitle("Smart E-Pharmacy");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(680);

        try {
            primaryStage.getIcons().add(
                    new Image(Objects.requireNonNull(
                            getClass().getResourceAsStream("/images/app-icon.png"))));
        } catch (Exception ignored) { /* icon is optional */ }

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
