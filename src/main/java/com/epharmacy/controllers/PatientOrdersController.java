package com.epharmacy.controllers;

import com.epharmacy.models.*;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.services.OrderService;
import com.epharmacy.services.PDFService;
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
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

/** Patient order history and tracking controller. */
public class PatientOrdersController implements Initializable, Refreshable {

    @FXML private TableView<Order>            ordersTable;
    @FXML private TableColumn<Order,Integer>  idCol;
    @FXML private TableColumn<Order,Double>   totalCol;
    @FXML private TableColumn<Order,String>   paymentCol;
    @FXML private TableColumn<Order,Order.Status> statusCol;
    @FXML private TableColumn<Order,LocalDateTime> dateCol;

    @FXML private TableView<OrderItem>            itemsTable;
    @FXML private TableColumn<OrderItem,String>   itemNameCol;
    @FXML private TableColumn<OrderItem,Integer>  itemQtyCol;
    @FXML private TableColumn<OrderItem,Double>   itemPriceCol, itemSubtotalCol;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnPdf;

    private final OrderService orderService = new OrderService();
    private final PDFService   pdfService   = new PDFService();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();
    private final ObservableList<OrderItem> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTables();
        ordersTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> loadOrderItems(sel));
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "My orders");
        ScreenUi.table(ordersTable, "No orders yet", "Your order history will appear here");
        ScreenUi.table(itemsTable, "Select an order", "Line items show when you select an order above");
        ScreenUi.actionButton(btnPdf, FontAwesomeSolid.FILE_PDF);
    }

    @Override
    public void refresh() {
        User patient = SessionManager.getInstance().getCurrentUser();
        if (patient == null) return;
        AsyncHelper.supplyAsync(
                () -> orderService.getPatientOrders(patient.getId()),
                orders::setAll);
    }

    private void setupTables() {
        if (idCol != null)      idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (totalCol != null)   totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        if (paymentCol != null) paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        if (statusCol != null)  statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        if (dateCol != null) {
            dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            dateCol.setCellFactory(col -> new TableCell<Order, LocalDateTime>() {
                @Override protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                    setText(FormatUtil.formatDateTime(((Order)getTableRow().getItem()).getCreatedAt()));
                }
            });
        }
        if (itemNameCol != null)     itemNameCol.setCellValueFactory(new PropertyValueFactory<>("medicineName"));
        if (itemQtyCol != null)      itemQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        if (itemPriceCol != null)    itemPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        if (itemSubtotalCol != null) itemSubtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        ordersTable.setItems(orders);
        itemsTable.setItems(items);
    }

    private void loadOrderItems(Order order) {
        if (order == null) { items.clear(); return; }
        AsyncHelper.supplyAsync(
                () -> orderService.getOrderItems(order.getId()),
                items::setAll);
    }

    @FXML private void exportPDF(ActionEvent e) {
        Order sel = ordersTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select","Select an order."); return; }
        try {
            Order full = orderService.findById(sel.getId());
            String path = System.getProperty("user.home") + "/Desktop/Invoice_" + full.getId() + ".pdf";
            pdfService.generateInvoice(full, path);
            AlertHelper.showInfo("PDF Saved","Invoice saved to:\n" + path);
        } catch (Exception ex) { AlertHelper.showError("PDF Error", ex.getMessage()); }
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) ordersTable.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }
}
