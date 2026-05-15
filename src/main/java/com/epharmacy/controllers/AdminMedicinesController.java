package com.epharmacy.controllers;

import com.epharmacy.models.Medicine;
import com.epharmacy.patterns.proxy.MedicineServiceProxy;
import com.epharmacy.services.MedicineService;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Medicine management controller (Admin + Pharmacist).
 * Proxy pattern enforces role-based access on write ops.
 * Builder pattern constructs Medicine from form fields.
 */
public class AdminMedicinesController implements Initializable, Refreshable {

    @FXML private TableView<Medicine>           medicineTable;
    @FXML private TableColumn<Medicine,Integer> idCol;
    @FXML private TableColumn<Medicine,String>  nameCol, categoryCol;
    @FXML private TableColumn<Medicine,LocalDate> expiryCol;
    @FXML private TableColumn<Medicine,Double>  priceCol;
    @FXML private TableColumn<Medicine,Integer> qtyCol;
    @FXML private TableColumn<Medicine,Boolean> rxCol;

    @FXML private TextField  searchField, nameField, categoryField;
    @FXML private TextField  priceField, quantityField, manufacturerField, imagePathField;
    @FXML private DatePicker expiryPicker;
    @FXML private CheckBox   requiresRxCheck;
    @FXML private TextArea   descriptionArea;

    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnSearch;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnBrowse;

    private final MedicineServiceProxy proxy =
            new MedicineServiceProxy(new MedicineService());
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private Medicine selectedMedicine;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        medicineTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> populateForm(sel));
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Medicine management");
        ScreenUi.table(medicineTable, "No medicines found", "Add a medicine or adjust your search");
        ScreenUi.actionButton(btnSearch, FontAwesomeSolid.SEARCH);
        ScreenUi.actionButton(btnAdd, FontAwesomeSolid.PLUS);
        ScreenUi.actionButton(btnUpdate, FontAwesomeSolid.EDIT);
        ScreenUi.actionButton(btnDelete, FontAwesomeSolid.TRASH);
        ScreenUi.actionButton(btnBrowse, FontAwesomeSolid.FOLDER_OPEN);
    }

    @Override
    public void refresh() {
        AsyncHelper.supplyAsync(proxy::getAllMedicines, medicines::setAll);
    }

    private void setupTable() {
        if (idCol != null)       idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (nameCol != null)     nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (categoryCol != null) categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        if (priceCol != null)    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        if (qtyCol != null)      qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        if (rxCol != null)       rxCol.setCellValueFactory(new PropertyValueFactory<>("requiresPrescription"));
        if (expiryCol != null) {
            expiryCol.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
            expiryCol.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                    Medicine m = (Medicine) getTableRow().getItem();
                    setText(FormatUtil.formatDate(m.getExpiryDate()));
                    if (m.isExpiringSoon()) getStyleClass().setAll("badge-lowstock");
                    else getStyleClass().clear();
                }
            });
        }
        medicineTable.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Medicine m, boolean empty) {
                super.updateItem(m, empty);
                if (m == null || empty) setStyle("");
                else if (m.isOutOfStock()) setStyle("-fx-background-color:#3f1d1d;");
                else if (m.isLowStock())   setStyle("-fx-background-color:#3f2e14;");
                else setStyle("");
            }
        });
        medicineTable.setItems(medicines);
    }

    private void loadMedicines() { refresh(); }

    private void populateForm(Medicine m) {
        if (m == null) return;
        selectedMedicine = m;
        if (nameField != null)         nameField.setText(m.getName());
        if (categoryField != null)     categoryField.setText(m.getCategory());
        if (priceField != null)        priceField.setText(String.valueOf(m.getPrice()));
        if (quantityField != null)     quantityField.setText(String.valueOf(m.getQuantity()));
        if (expiryPicker != null)      expiryPicker.setValue(m.getExpiryDate());
        if (manufacturerField != null) manufacturerField.setText(m.getManufacturer());
        if (requiresRxCheck != null)   requiresRxCheck.setSelected(m.isRequiresPrescription());
        if (descriptionArea != null)   descriptionArea.setText(m.getDescription());
        if (imagePathField != null)    imagePathField.setText(m.getImagePath() != null ? m.getImagePath() : "");
    }

    @FXML private void handleSearch(ActionEvent e) {
        String kw = searchField != null ? searchField.getText().trim() : "";
        medicines.setAll(kw.isEmpty() ? proxy.getAllMedicines() : proxy.search(kw));
    }

    @FXML private void handleAdd(ActionEvent e) {
        try { proxy.addMedicine(buildFromForm(0)); AlertHelper.showInfo("Success","Medicine added."); clearForm(); loadMedicines(); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }

    @FXML private void handleUpdate(ActionEvent e) {
        if (selectedMedicine == null) { AlertHelper.showWarning("Select","Select a medicine."); return; }
        try { proxy.updateMedicine(buildFromForm(selectedMedicine.getId())); AlertHelper.showInfo("Success","Updated."); loadMedicines(); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }

    @FXML private void handleDelete(ActionEvent e) {
        if (selectedMedicine == null) { AlertHelper.showWarning("Select","Select a medicine."); return; }
        if (!AlertHelper.showConfirmation("Confirm","Delete " + selectedMedicine.getName() + "?")) return;
        try { proxy.deleteMedicine(selectedMedicine.getId()); clearForm(); loadMedicines(); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }

    @FXML private void browseImage(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images","*.png","*.jpg","*.jpeg"));
        File f = fc.showOpenDialog(nameField.getScene().getWindow());
        if (f != null && imagePathField != null) imagePathField.setText(f.getAbsolutePath());
    }

    /** Builder pattern — constructs Medicine from form values. */
    private Medicine buildFromForm(int id) {
        return new Medicine.Builder()
                .id(id)
                .name(nameField != null ? nameField.getText().trim() : "")
                .category(categoryField != null ? categoryField.getText().trim() : "")
                .price(parseDouble(priceField))
                .quantity(parseInt(quantityField))
                .expiryDate(expiryPicker != null ? expiryPicker.getValue() : LocalDate.now().plusYears(1))
                .manufacturer(manufacturerField != null ? manufacturerField.getText().trim() : "")
                .requiresPrescription(requiresRxCheck != null && requiresRxCheck.isSelected())
                .description(descriptionArea != null ? descriptionArea.getText().trim() : "")
                .imagePath(imagePathField != null ? imagePathField.getText().trim() : null)
                .build();
    }

    private void clearForm() {
        selectedMedicine = null;
        if (nameField != null)         nameField.clear();
        if (categoryField != null)     categoryField.clear();
        if (priceField != null)        priceField.clear();
        if (quantityField != null)     quantityField.clear();
        if (expiryPicker != null)      expiryPicker.setValue(null);
        if (manufacturerField != null) manufacturerField.clear();
        if (requiresRxCheck != null)   requiresRxCheck.setSelected(false);
        if (descriptionArea != null)   descriptionArea.clear();
        if (imagePathField != null)    imagePathField.clear();
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) medicineTable.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }

    private double parseDouble(TextField f) {
        try { return f != null ? Double.parseDouble(f.getText().trim()) : 0; } catch (NumberFormatException e) { return 0; }
    }
    private int parseInt(TextField f) {
        try { return f != null ? Integer.parseInt(f.getText().trim()) : 0; } catch (NumberFormatException e) { return 0; }
    }
}
