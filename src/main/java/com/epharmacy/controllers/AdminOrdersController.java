package com.epharmacy.controllers;

import com.epharmacy.models.Order;
import com.epharmacy.patterns.command.ApproveOrderCommand;
import com.epharmacy.patterns.command.CancelOrderCommand;
import com.epharmacy.patterns.command.CommandInvoker;
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

/**
 * Order management controller for Admin/Pharmacist.
 * Uses Command pattern for approve/cancel actions.
 */
public class AdminOrdersController implements Initializable, Refreshable {

    @FXML private TableView<Order>             ordersTable;
    @FXML private TableColumn<Order,Integer>   idCol;
    @FXML private TableColumn<Order,String>       patientCol, paymentCol;
    @FXML private TableColumn<Order,Order.Status> statusCol;
    @FXML private TableColumn<Order,LocalDateTime> dateCol;
    @FXML private TableColumn<Order,Double>    totalCol;
    @FXML private ComboBox<String>             statusFilter;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnApprove;
    @FXML private Button btnDeliver;
    @FXML private Button btnCancel;
    @FXML private Button btnUndo;
    @FXML private Button btnPdf;

    private final OrderService   orderService = new OrderService();
    private final PDFService     pdfService   = new PDFService();
    /** Command invoker maintains undo history. */
    private final CommandInvoker invoker      = new CommandInvoker();
    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        if (statusFilter != null) {
            statusFilter.setItems(FXCollections.observableArrayList(
                    "ALL","PENDING","APPROVED","DELIVERED","CANCELLED"));
            statusFilter.setValue("ALL");
            statusFilter.setOnAction(e -> filterOrders());
        }
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Order management");
        ScreenUi.table(ordersTable, "No orders", "Orders matching your filter will appear here");
        ScreenUi.actionButton(btnApprove, FontAwesomeSolid.CHECK);
        ScreenUi.actionButton(btnDeliver, FontAwesomeSolid.TRUCK);
        ScreenUi.actionButton(btnCancel, FontAwesomeSolid.TIMES);
        ScreenUi.actionButton(btnUndo, FontAwesomeSolid.UNDO);
        ScreenUi.actionButton(btnPdf, FontAwesomeSolid.FILE_PDF);
    }

    @Override
    public void refresh() {
        String filter = statusFilter != null ? statusFilter.getValue() : "ALL";
        AsyncHelper.supplyAsync(() -> {
            if ("ALL".equals(filter)) return orderService.getAllOrders();
            return orderService.getAllOrders().stream()
                    .filter(o -> o.getStatus().name().equals(filter)).toList();
        }, orders::setAll);
    }

    private void setupTable() {
        if (idCol != null)      idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (patientCol != null) patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        if (totalCol != null)   totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        if (paymentCol != null) paymentCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        if (statusCol != null)  statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        if (dateCol != null) {
            dateCol.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            dateCol.setCellFactory(col -> new TableCell<>() {
                @Override protected void updateItem(LocalDateTime item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) { setText(null); return; }
                    setText(FormatUtil.formatDateTime(((Order)getTableRow().getItem()).getCreatedAt()));
                }
            });
        }

        // Status color coding
        if (statusCol != null) {
            statusCol.setCellFactory(col -> new TableCell<Order, Order.Status>() {
                @Override protected void updateItem(Order.Status item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                        getStyleClass().clear();
                        return;
                    }
                    Order o = (Order) getTableRow().getItem();
                    setText(o.getStatus().name());
                    getStyleClass().setAll(switch (o.getStatus()) {
                        case PENDING   -> "badge-pending";
                        case APPROVED  -> "badge-approved";
                        case DELIVERED -> "badge-delivered";
                        case CANCELLED -> "badge-cancelled";
                    });
                }
            });
        }
        ordersTable.setItems(orders);
    }

    private void loadOrders() { refresh(); }

    private void filterOrders() { refresh(); }

    /** COMMAND PATTERN: Approve selected order. */
    @FXML private void approveOrder(ActionEvent e) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Select","Select an order."); return; }
        invoker.execute(new ApproveOrderCommand(orderService, selected.getId()));
        loadOrders();
        AlertHelper.showInfo("Done","Order approved.");
    }

    /** COMMAND PATTERN: Cancel selected order. */
    @FXML private void cancelOrder(ActionEvent e) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Select","Select an order."); return; }
        if (!AlertHelper.showConfirmation("Confirm","Cancel order #" + selected.getId() + "?")) return;
        invoker.execute(new CancelOrderCommand(orderService, selected.getId()));
        loadOrders();
    }

    @FXML private void markDelivered(ActionEvent e) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Select","Select an order."); return; }
        orderService.updateStatus(selected.getId(), Order.Status.DELIVERED);
        loadOrders();
    }

    /** COMMAND PATTERN: Undo last action. */
    @FXML private void undoLast(ActionEvent e) {
        if (!invoker.canUndo()) { AlertHelper.showInfo("Undo","Nothing to undo."); return; }
        invoker.undoLast();
        loadOrders();
    }

    @FXML private void exportPDF(ActionEvent e) {
        Order selected = ordersTable.getSelectionModel().getSelectedItem();
        if (selected == null) { AlertHelper.showWarning("Select","Select an order."); return; }
        try {
            Order full = orderService.findById(selected.getId());
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
