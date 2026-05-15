package com.epharmacy.controllers;

import com.epharmacy.models.User;
import com.epharmacy.services.AuthService;
import com.epharmacy.ui.AppIcons;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.HeaderBarHelper;
import com.epharmacy.utils.NavStyler;
import com.epharmacy.utils.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.URL;
import java.util.ResourceBundle;

/** Controller for the Login screen. */
public class LoginController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private StackPane loginBrandIcon;
    @FXML private Label feature1;
    @FXML private Label feature2;
    @FXML private Label feature3;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HeaderBarHelper.configure(rootPane, "Smart E-Pharmacy", "Sign in", false);
        if (loginBrandIcon != null) {
            loginBrandIcon.getChildren().setAll(AppIcons.brand(FontAwesomeSolid.PILLS));
        }
        setFeature(feature1, FontAwesomeSolid.CHECK_CIRCLE, "Prescriptions and orders in one place");
        setFeature(feature2, FontAwesomeSolid.BELL, "Stock and expiry alerts");
        setFeature(feature3, FontAwesomeSolid.CHART_LINE, "Live sales and reports");
    }

    private void setFeature(Label label, FontAwesomeSolid icon, String text) {
        if (label == null) return;
        label.setText(text);
        label.setGraphic(AppIcons.nav(icon));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isBlank() || password.isBlank()) {
            AlertHelper.showError("Validation Error", "Please enter email and password.");
            return;
        }

        try {
            User user = authService.login(email, password);
            Stage stage = (Stage) emailField.getScene().getWindow();
            SceneNavigator.navigateToDashboard(stage);
        } catch (Exception e) {
            Throwable cause = e;
            while (cause.getCause() != null) cause = cause.getCause();
            String msg = cause.getMessage() != null ? cause.getMessage() : cause.getClass().getSimpleName();
            e.printStackTrace();
            AlertHelper.showError("Login Failed", msg);
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            SceneNavigator.navigateTo(stage, "/fxml/Register.fxml");
        } catch (Exception e) {
            AlertHelper.showError("Navigation Error", e.getMessage());
        }
    }
}
