package com.epharmacy.controllers;

import com.epharmacy.models.*;
import com.epharmacy.patterns.singleton.SessionManager;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/** Patient prescription upload and status tracking controller. */
public class PatientPrescriptionController implements Initializable, Refreshable {

    @FXML private TableView<Prescription>            table;
    @FXML private TableColumn<Prescription,Integer>            idCol;
    @FXML private TableColumn<Prescription,Prescription.Status> statusCol;
    @FXML private TableColumn<Prescription,LocalDateTime>      dateCol;
    @FXML private ImageView                          previewImage;
    @FXML private Label                              selectedFilePath;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnBrowse;
    @FXML private Button btnUpload;

    private final PrescriptionService service = new PrescriptionService();
    private final ObservableList<Prescription> list = FXCollections.observableArrayList();
    private String chosenImagePath;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (idCol != null)     idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (statusCol != null) statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (dateCol != null) {
            dateCol.setCellValueFactory(new PropertyValueFactory<>("uploadedAt"));
            dateCol.setCellFactory(col -> new TableCell<Prescription, LocalDateTime>() {
                @Override protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                    setText(FormatUtil.formatDateTime(((Prescription)getTableRow().getItem()).getUploadedAt()));
                }
            });
        }
        table.setItems(list);
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Prescriptions");
        ScreenUi.table(table, "No prescriptions", "Upload a prescription using the panel on the right");
        ScreenUi.actionButton(btnBrowse, FontAwesomeSolid.FOLDER_OPEN);
        ScreenUi.actionButton(btnUpload, FontAwesomeSolid.UPLOAD);
    }

    @Override
    public void refresh() {
        User p = SessionManager.getInstance().getCurrentUser();
        if (p == null) return;
        AsyncHelper.supplyAsync(() -> service.getByPatient(p.getId()), list::setAll);
    }

    private void loadList() { refresh(); }

    @FXML private void browseFile(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Prescription Image");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg","*.pdf"));
        File f = fc.showOpenDialog(table.getScene().getWindow());
        if (f != null) {
            chosenImagePath = f.getAbsolutePath();
            if (selectedFilePath != null) selectedFilePath.setText(f.getName());
            try { previewImage.setImage(new Image("file:" + chosenImagePath, 280, 280, true, true)); } catch (Exception ignored) {}
        }
    }

    @FXML private void uploadPrescription(ActionEvent e) {
        if (chosenImagePath == null || chosenImagePath.isBlank()) {
            AlertHelper.showWarning("No File","Please select a file first."); return;
        }
        User patient = SessionManager.getInstance().getCurrentUser();
        Prescription p = new Prescription();
        p.setPatientId(patient.getId());
        p.setImagePath(chosenImagePath);
        p.setStatus(Prescription.Status.PENDING);
        service.upload(p);
        AlertHelper.showInfo("Uploaded","Prescription submitted for review.");
        chosenImagePath = null;
        if (selectedFilePath != null) selectedFilePath.setText("No file selected");
        if (previewImage != null) previewImage.setImage(null);
        loadList();
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) table.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }
}
