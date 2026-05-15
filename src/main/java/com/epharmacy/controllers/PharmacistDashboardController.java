package com.epharmacy.controllers;

import com.epharmacy.models.User;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.services.AuthService;
import com.epharmacy.services.MedicineService;
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

/** Pharmacist dashboard home screen. */
public class PharmacistDashboardController implements Initializable, Refreshable {

    @FXML private BorderPane rootPane;
    @FXML private VBox mainContent;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Label welcomeLabel;
    @FXML private Label lowStockCount;
    @FXML private Label pendingOrderCount;
    @FXML private Label pendingRxCount;
    @FXML private Label totalMedicines;
    @FXML private VBox statLowStock;
    @FXML private VBox statOrders;
    @FXML private VBox statRx;
    @FXML private VBox statMeds;
    @FXML private Button navDashboard;
    @FXML private Button navMedicines;
    @FXML private Button navOrders;
    @FXML private Button navPrescriptions;
    @FXML private Button navLogout;
    @FXML private Button actionMedicines;
    @FXML private Button actionOrders;
    @FXML private Button actionRx;

    private final MedicineService medicineService = new MedicineService();
    private final OrderService orderService = new OrderService();
    private final PrescriptionService prescriptionService = new PrescriptionService();
    private final AuthService authService = new AuthService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        User user = SessionManager.getInstance().getCurrentUser();
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + (user != null ? user.getName() : "Pharmacist"));
        }
        setupUi();
    }

    private void setupUi() {
        DashboardUi.initShell(rootPane, "Smart E-Pharmacy", "Pharmacist · Overview");
        if (sidebarBrandMark != null) {
            sidebarBrandMark.getChildren().setAll(AppIcons.brand(FontAwesomeSolid.PILLS));
        }
        mountStatIcon(statLowStock, FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        mountStatIcon(statOrders, FontAwesomeSolid.SHOPPING_CART);
        mountStatIcon(statRx, FontAwesomeSolid.FILE_MEDICAL);
        mountStatIcon(statMeds, FontAwesomeSolid.PILLS);
        DashboardUi.applyNavIcons(
                new DashboardUi.NavItem(navDashboard, FontAwesomeSolid.HOME),
                new DashboardUi.NavItem(navMedicines, FontAwesomeSolid.PILLS),
                new DashboardUi.NavItem(navOrders, FontAwesomeSolid.SHOPPING_CART),
                new DashboardUi.NavItem(navPrescriptions, FontAwesomeSolid.FILE_MEDICAL),
                new DashboardUi.NavItem(navLogout, FontAwesomeSolid.SIGN_OUT_ALT)
        );
        DashboardUi.wireSidebar(navDashboard, navMedicines, navOrders, navPrescriptions, navLogout);
        NavStyler.action(actionMedicines, FontAwesomeSolid.PILLS);
        NavStyler.action(actionOrders, FontAwesomeSolid.SHOPPING_CART);
        NavStyler.action(actionRx, FontAwesomeSolid.FILE_MEDICAL);
        DashboardUi.animateContent(mainContent, statLowStock, statOrders, statRx, statMeds);
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
        AsyncHelper.supplyAsync(() -> new int[] {
                medicineService.getLowStockMedicines().size(),
                (int) orderService.getAllOrders().stream()
                        .filter(o -> o.getStatus() == com.epharmacy.models.Order.Status.PENDING).count(),
                prescriptionService.getPending().size(),
                medicineService.getTotalCount()
        }, s -> {
            if (lowStockCount != null) lowStockCount.setText(String.valueOf(s[0]));
            if (pendingOrderCount != null) pendingOrderCount.setText(String.valueOf(s[1]));
            if (pendingRxCount != null) pendingRxCount.setText(String.valueOf(s[2]));
            if (totalMedicines != null) totalMedicines.setText(String.valueOf(s[3]));
        });
    }

    @FXML private void goToMedicines(ActionEvent e) { navigate("/fxml/AdminMedicines.fxml"); }
    @FXML private void goToOrders(ActionEvent e) { navigate("/fxml/AdminOrders.fxml"); }
    @FXML private void goToPrescriptions(ActionEvent e) { navigate("/fxml/AdminPrescriptions.fxml"); }

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
            AlertHelper.showError("Navigation Error", ex.getMessage());
        }
    }
}
