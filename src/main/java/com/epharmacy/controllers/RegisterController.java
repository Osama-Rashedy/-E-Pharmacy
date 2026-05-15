package com.epharmacy.controllers;

import com.epharmacy.services.AuthService;
import com.epharmacy.ui.AppIcons;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.HeaderBarHelper;
import com.epharmacy.utils.SceneNavigator;
import com.epharmacy.utils.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.URL;
import java.util.ResourceBundle;

/** Patient self-registration controller. */
public class RegisterController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private StackPane loginBrandIcon;
    @FXML private TextField     nameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private TextField     phoneField;
    @FXML private TextField     addressField;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        HeaderBarHelper.configure(rootPane, "Smart E-Pharmacy", "Create account", false);
        if (loginBrandIcon != null) {
            loginBrandIcon.getChildren().setAll(AppIcons.brand(FontAwesomeSolid.USER_PLUS));
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmField.getText();
        String phone    = phoneField.getText().trim();
        String address  = addressField.getText().trim();

        // Validation
        if (!Validator.isNotEmpty(name)) { AlertHelper.showError("Validation","Name is required."); return; }
        if (!Validator.isEmailValid(email)) { AlertHelper.showError("Validation","Invalid email address."); return; }
        if (!Validator.isPasswordStrong(password)) { AlertHelper.showError("Validation","Password must be at least 6 characters."); return; }
        if (!password.equals(confirm)) { AlertHelper.showError("Validation","Passwords do not match."); return; }

        try {
            authService.registerPatient(name, email, password, phone, address);
            AlertHelper.showInfo("Success", "Account created! Please log in.");
            goToLogin();
        } catch (Exception e) {
            AlertHelper.showError("Registration Failed", e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            SceneNavigator.navigateTo(stage, "/fxml/Login.fxml");
        } catch (Exception e) {
            AlertHelper.showError("Navigation Error", e.getMessage());
        }
    }
}
