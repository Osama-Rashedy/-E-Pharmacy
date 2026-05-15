package com.epharmacy.controllers;

import com.epharmacy.dao.UserDAO;
import com.epharmacy.models.User;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.services.AuthService;
import com.epharmacy.services.MedicineService;
import com.epharmacy.services.OrderService;
import com.epharmacy.services.ReportService;
import com.epharmacy.ui.StatCardUi;
import com.epharmacy.ui.UiComponents;
import com.epharmacy.ui.AppIcons;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.AsyncHelper;
import com.epharmacy.utils.DashboardUi;
import com.epharmacy.utils.Refreshable;
import com.epharmacy.utils.NavStyler;
import com.epharmacy.utils.SceneNavigator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/** Admin dashboard controller. */
public class AdminDashboardController implements Initializable, Refreshable {

    @FXML private Label welcomeLabel;
    @FXML private Label pharmacistCount;
    @FXML private Label patientCount;
    @FXML private Label medicineCount;
    @FXML private Label orderCount;
    @FXML private Label totalRevenue;

    @FXML private TableView<User>           pharmacistTable;
    @FXML private TableColumn<User,Integer> pharmaIdCol;
    @FXML private TableColumn<User,String>  pharmaNameCol;
    @FXML private TableColumn<User,String>  pharmaEmailCol;
    @FXML private TableColumn<User,String>  pharmaPhoneCol;

    @FXML private TextField     pharmaNameField;
    @FXML private TextField     pharmaEmailField;
    @FXML private PasswordField pharmaPasswordField;
    @FXML private TextField     pharmaPhoneField;

    @FXML private TableView<User>           patientTable;
    @FXML private TableColumn<User,Integer> patientIdCol;
    @FXML private TableColumn<User,String>  patientNameCol;
    @FXML private TableColumn<User,String>  patientEmailCol;
    @FXML private TableColumn<User,String>  patientPhoneCol;

    @FXML private BarChart<String,Number> revenueChart;
    @FXML private PieChart               categoryPie;

    @FXML private BorderPane rootPane;
    @FXML private VBox mainContent;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button navDashboard;
    @FXML private Button navMedicines;
    @FXML private Button navOrders;
    @FXML private Button navPrescriptions;
    @FXML private Button navReports;
    @FXML private Button navLogout;
    @FXML private Button btnAddPharma;
    @FXML private Button btnDeletePharma;
    @FXML private VBox statPharma;
    @FXML private VBox statPatients;
    @FXML private VBox statMedicines;
    @FXML private VBox statOrders;
    @FXML private VBox statRevenue;

    private final AuthService     authService     = new AuthService();
    private final UserDAO         userDAO         = new UserDAO();
    private final MedicineService medicineService = new MedicineService();
    private final OrderService    orderService    = new OrderService();
    private final ReportService   reportService   = new ReportService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            User admin = SessionManager.getInstance().getCurrentUser();
            if (welcomeLabel != null)
                welcomeLabel.setText("Welcome, " + (admin != null ? admin.getName() : "Admin") + "!");
            setupTables();
            setupUi();
        } catch (Exception e) { System.err.println("[Admin init] " + e.getMessage()); }

    }

    private void setupUi() {
        DashboardUi.initShell(rootPane, "Smart E-Pharmacy", "Admin · Overview");
        if (sidebarBrandMark != null) {
            sidebarBrandMark.getChildren().setAll(AppIcons.brand(FontAwesomeSolid.PILLS));
        }
        mountStatIcon(statPharma, FontAwesomeSolid.USER_MD);
        mountStatIcon(statPatients, FontAwesomeSolid.USER);
        mountStatIcon(statMedicines, FontAwesomeSolid.PILLS);
        mountStatIcon(statOrders, FontAwesomeSolid.SHOPPING_CART);
        mountStatIcon(statRevenue, FontAwesomeSolid.DOLLAR_SIGN);
        DashboardUi.applyNavIcons(
                new DashboardUi.NavItem(navDashboard, FontAwesomeSolid.HOME),
                new DashboardUi.NavItem(navMedicines, FontAwesomeSolid.PILLS),
                new DashboardUi.NavItem(navOrders, FontAwesomeSolid.SHOPPING_CART),
                new DashboardUi.NavItem(navPrescriptions, FontAwesomeSolid.FILE_MEDICAL),
                new DashboardUi.NavItem(navReports, FontAwesomeSolid.CHART_BAR),
                new DashboardUi.NavItem(navLogout, FontAwesomeSolid.SIGN_OUT_ALT)
        );
        DashboardUi.wireSidebar(navDashboard, navMedicines, navOrders, navPrescriptions, navReports, navLogout);
        DashboardUi.animateContent(mainContent, statPharma, statPatients, statMedicines, statOrders, statRevenue);
        UiComponents.bindTablePlaceholder(pharmacistTable, "No pharmacists yet", "Add team members using the form above");
        UiComponents.bindTablePlaceholder(patientTable, "No patients registered", "Patient accounts will appear here");
        NavStyler.action(btnAddPharma, FontAwesomeSolid.PLUS);
        NavStyler.action(btnDeletePharma, FontAwesomeSolid.TRASH);
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
        try { loadStatsAsync(); } catch (Exception e) { System.err.println("[Admin stats] " + e.getMessage()); }
        try { loadPharmacistsAsync(); } catch (Exception e) { System.err.println("[Admin pharma] " + e.getMessage()); }
        try { loadPatientsAsync(); } catch (Exception e) { System.err.println("[Admin patient] " + e.getMessage()); }
        try { loadChartsAsync(); } catch (Exception e) { System.err.println("[Admin charts] " + e.getMessage()); }
    }

    private void loadStatsAsync() {
        AsyncHelper.supplyAsync(() -> {
            try {
                return new Object[] {
                        userDAO.countByRole(User.Role.PHARMACIST),
                        userDAO.countByRole(User.Role.PATIENT),
                        medicineService.getTotalCount(),
                        orderService.getTotalOrderCount(),
                        orderService.getTotalRevenue()
                };
            } catch (SQLException e) {
                return new Object[] { 0, 0, 0, 0, 0.0 };
            }
        }, r -> {
            if (pharmacistCount != null) pharmacistCount.setText(String.valueOf(r[0]));
            if (patientCount != null)    patientCount.setText(String.valueOf(r[1]));
            if (medicineCount != null)   medicineCount.setText(String.valueOf(r[2]));
            if (orderCount != null)      orderCount.setText(String.valueOf(r[3]));
            if (totalRevenue != null)    totalRevenue.setText(String.format("$%.2f", (Double) r[4]));
        });
    }

    private void loadPharmacistsAsync() {
        AsyncHelper.supplyAsync(() -> {
            try { return userDAO.findByRole(User.Role.PHARMACIST); }
            catch (SQLException e) { return Collections.<User>emptyList(); }
        }, list -> { if (pharmacistTable != null) pharmacistTable.setItems(FXCollections.observableArrayList(list)); });
    }

    private void loadPatientsAsync() {
        AsyncHelper.supplyAsync(() -> {
            try { return userDAO.findByRole(User.Role.PATIENT); }
            catch (SQLException e) { return Collections.<User>emptyList(); }
        }, list -> { if (patientTable != null) patientTable.setItems(FXCollections.observableArrayList(list)); });
    }

    private void loadChartsAsync() {
        AsyncHelper.supplyAsync(reportService::getMonthlyRevenue, revenue -> {
            try {
                if (revenueChart == null) return;
                revenueChart.getData().clear();
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Revenue ($)");
                revenue.forEach((m, val) -> series.getData().add(new XYChart.Data<>(m, val)));
                revenueChart.getData().add(series);
            } catch (Exception e) { System.err.println("[Charts] Revenue: " + e.getMessage()); }
        });
        AsyncHelper.supplyAsync(reportService::getMedicinesByCategory, byCat -> {
            try {
                if (categoryPie == null) return;
                categoryPie.getData().clear();
                byCat.forEach((cat, cnt) -> categoryPie.getData().add(new PieChart.Data(cat, cnt)));
            } catch (Exception e) { System.err.println("[Charts] Category: " + e.getMessage()); }
        });
    }

    private void setupTables() {
        if (pharmaIdCol != null)    pharmaIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (pharmaNameCol != null)  pharmaNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (pharmaEmailCol != null) pharmaEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (pharmaPhoneCol != null) pharmaPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        if (patientIdCol != null)    patientIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (patientNameCol != null)  patientNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (patientEmailCol != null) patientEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        if (patientPhoneCol != null) patientPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
    }

    private void loadStats() { loadStatsAsync(); }

    private void loadPharmacists() { loadPharmacistsAsync(); }

    private void loadPatients() { loadPatientsAsync(); }

    @FXML
    private void addPharmacist(ActionEvent event) {
        try {
            authService.addPharmacist(
                    pharmaNameField.getText().trim(),
                    pharmaEmailField.getText().trim(),
                    pharmaPasswordField.getText(),
                    pharmaPhoneField.getText().trim());
            AlertHelper.showInfo("Success", "Pharmacist added.");
            clearPharmaForm();
            loadPharmacists();
            loadStats();
        } catch (Exception e) { AlertHelper.showError("Error", e.getMessage()); }
    }

    @FXML
    private void deletePharmacist(ActionEvent event) {
        User sel = pharmacistTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select","Select a pharmacist."); return; }
        if (!AlertHelper.showConfirmation("Confirm","Delete " + sel.getName() + "?")) return;
        try { authService.deleteUser(sel.getId()); loadPharmacists(); loadStats(); }
        catch (Exception e) { AlertHelper.showError("Error", e.getMessage()); }
    }

    private void clearPharmaForm() {
        if (pharmaNameField != null)     pharmaNameField.clear();
        if (pharmaEmailField != null)    pharmaEmailField.clear();
        if (pharmaPasswordField != null) pharmaPasswordField.clear();
        if (pharmaPhoneField != null)    pharmaPhoneField.clear();
    }

    @FXML private void goToMedicines(ActionEvent e)     { navigate("/fxml/AdminMedicines.fxml"); }
    @FXML private void goToOrders(ActionEvent e)        { navigate("/fxml/AdminOrders.fxml"); }
    @FXML private void goToPrescriptions(ActionEvent e) { navigate("/fxml/AdminPrescriptions.fxml"); }
    @FXML private void goToReports(ActionEvent e)       { navigate("/fxml/AdminReports.fxml"); }

    @FXML
    private void logout(ActionEvent e) {
        authService.logout();
        navigate("/fxml/Login.fxml");
    }

    private void navigate(String fxml) {
        try {
            // Use any non-null FXML node to get the stage
            Stage stage = null;
            if (welcomeLabel != null && welcomeLabel.getScene() != null)
                stage = (Stage) welcomeLabel.getScene().getWindow();
            if (stage == null && pharmacistTable != null && pharmacistTable.getScene() != null)
                stage = (Stage) pharmacistTable.getScene().getWindow();
            if (stage != null) SceneNavigator.navigateTo(stage, fxml);
        } catch (Exception ex) { AlertHelper.showError("Navigation Error", ex.getMessage()); }
    }
}
