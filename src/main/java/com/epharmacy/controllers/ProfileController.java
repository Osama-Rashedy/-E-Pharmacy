package com.epharmacy.controllers;

import com.epharmacy.models.User;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.services.AuthService;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.SceneNavigator;
import com.epharmacy.utils.ScreenUi;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

/** User profile editor controller. */
public class ProfileController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnSave;

    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Profile settings");
        ScreenUi.actionButton(btnSave, FontAwesomeSolid.SAVE);
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            if (nameField != null)    nameField.setText(user.getName());
            if (emailField != null)   { emailField.setText(user.getEmail()); emailField.setEditable(false); }
            if (phoneField != null)   phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
            if (addressField != null) addressField.setText(user.getAddress() != null ? user.getAddress() : "");
        }
    }

    @FXML private void saveProfile(ActionEvent e) {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        if (nameField != null)    user.setName(nameField.getText().trim());
        if (phoneField != null)   user.setPhone(phoneField.getText().trim());
        if (addressField != null) user.setAddress(addressField.getText().trim());
        try {
            authService.updateProfile(user);
            AlertHelper.showInfo("Saved","Profile updated successfully.");
        } catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) nameField.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }
}
