package com.epharmacy.controllers;

import com.epharmacy.models.User;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.services.AuthService;
import com.epharmacy.services.OrderService;
import com.epharmacy.services.PrescriptionService;
import com.epharmacy.ui.AppIcons;
import com.epharmacy.ui.StatCardUi;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.AsyncHelper;
import com.epharmacy.utils.DashboardUi;
import com.epharmacy.utils.NavStyler;
import com.epharmacy.utils.Refreshable;
import com.epharmacy.utils.SceneNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.URL;
import java.util.ResourceBundle;

/** Patient dashboard home screen with quick stats. */
public class PatientDashboardController implements Initializable, Refreshable {

    @FXML private BorderPane rootPane;
    @FXML private VBox mainContent;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Label welcomeLabel;
    @FXML private Label orderCountLabel;
    @FXML private Label rxCountLabel;
    @FXML private VBox statOrders;
    @FXML private VBox statRx;
    @FXML private Button navHome;
    @FXML private Button navBrowse;
    @FXML private Button navOrders;
    @FXML private Button navRx;
    @FXML private Button navProfile;
    @FXML private Button navLogout;
    @FXML private Button actionBrowse;
    @FXML private Button actionOrders;
    @FXML private Button actionRx;

    private final OrderService orderService = new OrderService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = SessionManager.getInstance().getCurrentUser();
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + (user != null ? user.getName() : "Patient"));
        }
        setupUi();
    }

    private void setupUi() {
        DashboardUi.initShell(rootPane, "Smart E-Pharmacy", "Patient · Home");
        if (sidebarBrandMark != null) {
            sidebarBrandMark.getChildren().setAll(AppIcons.brand(FontAwesomeSolid.PILLS));
        }
        mountStatIcon(statOrders, FontAwesomeSolid.SHOPPING_CART);
        mountStatIcon(statRx, FontAwesomeSolid.FILE_MEDICAL);
        DashboardUi.applyNavIcons(
                new DashboardUi.NavItem(navHome, FontAwesomeSolid.HOME),
                new DashboardUi.NavItem(navBrowse, FontAwesomeSolid.SEARCH),
                new DashboardUi.NavItem(navOrders, FontAwesomeSolid.SHOPPING_CART),
                new DashboardUi.NavItem(navRx, FontAwesomeSolid.FILE_MEDICAL),
                new DashboardUi.NavItem(navProfile, FontAwesomeSolid.USER),
                new DashboardUi.NavItem(navLogout, FontAwesomeSolid.SIGN_OUT_ALT)
        );
        DashboardUi.wireSidebar(navHome, navBrowse, navOrders, navRx, navProfile, navLogout);
        NavStyler.action(actionBrowse, FontAwesomeSolid.SEARCH);
        NavStyler.action(actionOrders, FontAwesomeSolid.SHOPPING_CART);
        NavStyler.action(actionRx, FontAwesomeSolid.UPLOAD);
        DashboardUi.animateContent(mainContent, statOrders, statRx);
    }

    private void mountStatIcon(VBox card, FontAwesomeSolid icon) {
        if (card == null || card.getChildren().isEmpty()) return;
        if (card.getChildren().get(0) instanceof HBox header && !header.getChildren().isEmpty()
                && header.getChildren().get(0) instanceof StackPane host) {
            StatCardUi.mountIcon(host, icon);
        }
    }

    @Override
    public void refresh() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;
        AsyncHelper.supplyAsync(() -> new int[] {
                orderService.getPatientOrders(user.getId()).size(),
                prescriptionService.getByPatient(user.getId()).size()
        }, counts -> {
            if (orderCountLabel != null) orderCountLabel.setText(String.valueOf(counts[0]));
            if (rxCountLabel != null) rxCountLabel.setText(String.valueOf(counts[1]));
        });
    }

    @FXML private void goToBrowse(ActionEvent e) { navigate("/fxml/PatientBrowse.fxml"); }
    @FXML private void goToOrders(ActionEvent e) { navigate("/fxml/PatientOrders.fxml"); }
    @FXML private void goToPrescription(ActionEvent e) { navigate("/fxml/PatientPrescription.fxml"); }
    @FXML private void goToProfile(ActionEvent e) { navigate("/fxml/Profile.fxml"); }

    @FXML
    private void logout(ActionEvent e) {
        authService.logout();
        navigate("/fxml/Login.fxml");
    }

    private void navigate(String fxml) {
        try {
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            SceneNavigator.navigateTo(stage, fxml);
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (ex.getCause() != null) {
                msg += "\nCause: " + ex.getCause().getClass().getSimpleName() + ": " + ex.getCause().getMessage();
            }
            AlertHelper.showError("Navigation Error", msg);
        }
    }
}
