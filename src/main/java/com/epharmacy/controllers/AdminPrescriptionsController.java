package com.epharmacy.controllers;

import com.epharmacy.models.Prescription;
import com.epharmacy.services.PrescriptionService;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.AsyncHelper;
import com.epharmacy.utils.FormatUtil;
import com.epharmacy.utils.Refreshable;
import com.epharmacy.utils.SceneNavigator;
import com.epharmacy.utils.ScreenUi;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/** Prescription review controller for Pharmacist/Admin. */
public class AdminPrescriptionsController implements Initializable, Refreshable {

    @FXML private TableView<Prescription>             table;
    @FXML private TableColumn<Prescription,Integer>   idCol;
    @FXML private TableColumn<Prescription,String>       patientCol;
    @FXML private TableColumn<Prescription,Prescription.Status> statusCol;
    @FXML private TableColumn<Prescription,LocalDateTime> dateCol;
    @FXML private ImageView                           prescriptionImage;
    @FXML private TextArea                            notesArea;
    @FXML private ComboBox<String>                    statusFilter;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnApprove;
    @FXML private Button btnReject;

    private final PrescriptionService service = new PrescriptionService();
    private final ObservableList<Prescription> list = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (idCol != null)      idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (patientCol != null) patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        if (statusCol != null)  statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (dateCol != null) {
            dateCol.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));
            dateCol.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                    setText(FormatUtil.formatDateTime(((Prescription)getTableRow().getItem()).getUploadedAt()));
                }
            });
        }
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList("ALL","PENDING","APPROVED","REJECTED"));
            statusFilter.setValue("ALL");
            statusFilter.setOnAction(e -> refresh());
        }
        table.setItems(list);
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> showPreview(sel));
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Prescription review");
        ScreenUi.table(table, "No prescriptions", "Pending uploads will appear here");
        ScreenUi.actionButton(btnApprove, FontAwesomeSolid.CHECK);
        ScreenUi.actionButton(btnReject, FontAwesomeSolid.TIMES);
    }

    @Override
    public void refresh() {
        String filter = statusFilter != null ? statusFilter.getValue() : "ALL";
        AsyncHelper.supplyAsync(() -> {
            var data = "PENDING".equals(filter) ? service.getPending() : service.getAll();
            if (!"ALL".equals(filter) && !"PENDING".equals(filter)) {
                Prescription.Status s = Prescription.Status.valueOf(filter);
                return data.stream().filter(p -> p.getStatus() == s).toList();
            }
            return data;
        }, list::setAll);
    }

    private void loadList() { refresh(); }

    private void showPreview(Prescription p) {
        if (p == null || prescriptionImage == null) return;
        try {
            if (p.getImagePath() != null && !p.getImagePath().isBlank()) {
                prescriptionImage.setImage(new Image("file:" + p.getImagePath(), 300, 300, true, true));
            }
        } catch (Exception ignored) {}
    }

    @FXML private void approve(ActionEvent e) {
        Prescription sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select","Select a prescription."); return; }
        service.approve(sel.getId(), notesArea != null ? notesArea.getText() : "Approved");
        AlertHelper.showInfo("Done","Prescription approved.");
        loadList();
    }

    @FXML private void reject(ActionEvent e) {
        Prescription sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select","Select a prescription."); return; }
        service.reject(sel.getId(), notesArea != null ? notesArea.getText() : "Rejected");
        AlertHelper.showInfo("Done","Prescription rejected.");
        loadList();
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) table.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }
}
