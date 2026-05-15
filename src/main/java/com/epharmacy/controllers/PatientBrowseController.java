package com.epharmacy.controllers;

import com.epharmacy.models.*;
import com.epharmacy.patterns.decorator.*;
import com.epharmacy.patterns.singleton.SessionManager;
import com.epharmacy.patterns.strategy.*;
import com.epharmacy.services.MedicineService;
import com.epharmacy.services.OrderService;
import com.epharmacy.utils.AlertHelper;
import com.epharmacy.utils.AsyncHelper;
import com.epharmacy.utils.Refreshable;
import com.epharmacy.utils.SceneNavigator;
import com.epharmacy.utils.ScreenUi;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Patient medicine browse + cart + checkout controller.
 * Uses Strategy pattern for payment and Decorator for invoice.
 */
public class PatientBrowseController implements Initializable, Refreshable {

    // ── Browse table ─────────────────────────────────────────────
    @FXML private TableView<Medicine>           medicineTable;
    @FXML private TableColumn<Medicine,String>  mNameCol, mCategoryCol, mManufCol;
    @FXML private TableColumn<Medicine,Double>  mPriceCol;
    @FXML private TableColumn<Medicine,Integer> mQtyCol;
    @FXML private TableColumn<Medicine,Boolean> mRxCol;
    @FXML private TextField                     searchField;
    @FXML private Spinner<Integer>              qtySpinner;

    // ── Cart table ───────────────────────────────────────────────
    @FXML private TableView<CartItem>           cartTable;
    @FXML private TableColumn<CartItem,String>  cNameCol;
    @FXML private TableColumn<CartItem,Double>  cPriceCol, cSubtotalCol;
    @FXML private TableColumn<CartItem,Integer> cQtyCol;
    @FXML private Label                         totalLabel;
    @FXML private ComboBox<String>              paymentCombo;
    @FXML private TextArea                     invoiceSummary;
    @FXML private BorderPane rootPane;
    @FXML private StackPane sidebarBrandMark;
    @FXML private Button btnBack;
    @FXML private Button btnSearch;
    @FXML private Button btnAddCart;
    @FXML private Button btnRemoveCart;
    @FXML private Button btnCheckout;

    private final MedicineService medicineService = new MedicineService();
    private final OrderService    orderService    = new OrderService();
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private final ObservableList<CartItem> cart      = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupMedicineTable();
        setupCartTable();
        if (paymentCombo != null)
            paymentCombo.setItems(FXCollections.observableArrayList("Cash","Credit Card","Wallet"));
        if (qtySpinner != null)
            qtySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 1));
        ScreenUi.setup(rootPane, sidebarBrandMark, btnBack,
                "Smart E-Pharmacy", "Browse & shop");
        ScreenUi.table(medicineTable, "No medicines available", "Try a different search term");
        ScreenUi.table(cartTable, "Cart is empty", "Select medicines and add them to your cart");
        ScreenUi.actionButton(btnSearch, FontAwesomeSolid.SEARCH);
        ScreenUi.actionButton(btnAddCart, FontAwesomeSolid.CART_PLUS);
        ScreenUi.actionButton(btnRemoveCart, FontAwesomeSolid.TRASH);
        ScreenUi.actionButton(btnCheckout, FontAwesomeSolid.CREDIT_CARD);
    }

    @Override
    public void refresh() {
        String kw = searchField != null ? searchField.getText().trim() : "";
        AsyncHelper.supplyAsync(
                () -> kw.isEmpty() ? medicineService.getAllMedicines() : medicineService.search(kw),
                medicines::setAll);
    }

    private void setupMedicineTable() {
        if (mNameCol != null)     mNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (mCategoryCol != null) mCategoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        if (mPriceCol != null)    mPriceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        if (mQtyCol != null)      mQtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        if (mManufCol != null)    mManufCol.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
        if (mRxCol != null)       mRxCol.setCellValueFactory(new PropertyValueFactory<>("requiresPrescription"));
        medicineTable.setItems(medicines);
    }

    private void setupCartTable() {
        if (cNameCol != null)    cNameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMedicineName()));
        if (cPriceCol != null)   cPriceCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getUnitPrice()).asObject());
        if (cQtyCol != null)     cQtyCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        if (cSubtotalCol != null) cSubtotalCol.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getSubtotal()).asObject());
        cartTable.setItems(cart);
    }

    @FXML private void handleSearch(ActionEvent e) { refresh(); }

    @FXML private void addToCart(ActionEvent e) {
        Medicine sel = medicineTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select","Select a medicine first."); return; }
        int qty = qtySpinner != null ? qtySpinner.getValue() : 1;
        if (sel.getQuantity() < qty) { AlertHelper.showError("Stock","Insufficient stock."); return; }

        // Check if already in cart → increase qty
        for (CartItem ci : cart) {
            if (ci.getMedicine().getId() == sel.getId()) {
                ci.setQuantity(ci.getQuantity() + qty);
                cartTable.refresh();
                updateTotal();
                return;
            }
        }
        cart.add(new CartItem(sel, qty));
        updateTotal();
    }

    @FXML private void removeFromCart(ActionEvent e) {
        CartItem sel = cartTable.getSelectionModel().getSelectedItem();
        if (sel == null) { AlertHelper.showWarning("Select","Select a cart item."); return; }
        cart.remove(sel);
        updateTotal();
    }

    private void updateTotal() {
        double subtotal = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        if (totalLabel != null) totalLabel.setText(String.format("$%.2f", subtotal));
        // Show decorated invoice preview using Decorator pattern
        if (invoiceSummary != null) {
            Invoice inv = new BasicInvoice(subtotal, "Subtotal");
            inv = new TaxDecorator(inv, 0.14);        // 14% VAT
            invoiceSummary.setText(inv.getDescription()
                    + String.format("%nTotal (incl. tax): $%.2f", inv.getTotal()));
        }
    }

    @FXML private void checkout(ActionEvent e) {
        if (cart.isEmpty()) { AlertHelper.showWarning("Cart","Your cart is empty."); return; }
        String payMethod = paymentCombo != null ? paymentCombo.getValue() : "Cash";
        if (payMethod == null) { AlertHelper.showWarning("Payment","Select a payment method."); return; }

        // Strategy pattern — choose payment implementation
        PaymentStrategy strategy = switch (payMethod) {
            case "Credit Card" -> new CreditCardPayment("4111111111111111");
            case "Wallet"      -> new WalletPayment(1000.0);
            default            -> new CashPayment();
        };

        User patient = SessionManager.getInstance().getCurrentUser();
        try {
            Order order = orderService.placeOrder(patient.getId(), new ArrayList<>(cart), strategy);
            cart.clear();
            updateTotal();
            AlertHelper.showInfo("Order Placed",
                    "Order #" + order.getId() + " placed successfully!\nPayment: " + payMethod);
        } catch (Exception ex) { AlertHelper.showError("Checkout Error", ex.getMessage()); }
    }

    @FXML private void goBack(ActionEvent e) {
        try { SceneNavigator.navigateToDashboard((Stage) medicineTable.getScene().getWindow()); }
        catch (Exception ex) { AlertHelper.showError("Error", ex.getMessage()); }
    }
}
