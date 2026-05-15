package com.epharmacy.services;

import com.epharmacy.dao.MedicineDAO;
import com.epharmacy.dao.OrderDAO;
import com.epharmacy.dao.OrderItemDAO;
import com.epharmacy.models.CartItem;
import com.epharmacy.models.Medicine;
import com.epharmacy.models.Order;
import com.epharmacy.models.OrderItem;
import com.epharmacy.patterns.strategy.PaymentStrategy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Order service: places orders, updates status, deducts inventory.
 * Uses Strategy pattern for payment processing.
 */
public class OrderService {

    private final OrderDAO     orderDAO     = new OrderDAO();
    private final OrderItemDAO orderItemDAO = new OrderItemDAO();
    private final MedicineDAO  medicineDAO  = new MedicineDAO();

    // ── PLACE ORDER ──────────────────────────────────────────────────
    /**
     * Places a new order from the patient's cart.
     * Uses the provided PaymentStrategy (Strategy pattern).
     */
    public Order placeOrder(int patientId, List<CartItem> cartItems,
                            PaymentStrategy paymentStrategy) throws Exception {

        if (cartItems.isEmpty()) throw new Exception("Cart is empty.");

        // Build order items + validate stock
        List<OrderItem> items = new ArrayList<>();
        double total = 0;
        for (CartItem ci : cartItems) {
            Medicine med = medicineDAO.findById(ci.getMedicine().getId())
                    .orElseThrow(() -> new Exception("Medicine not found: " + ci.getMedicine().getName()));
            if (med.getQuantity() < ci.getQuantity())
                throw new Exception("Insufficient stock for: " + med.getName());

            OrderItem oi = new OrderItem(med.getId(), med.getName(),
                    ci.getQuantity(), med.getPrice());
            items.add(oi);
            total += oi.getSubtotal();
        }

        // Process payment via Strategy
        boolean paid = paymentStrategy.pay(total);
        if (!paid) throw new Exception("Payment failed. Please try again.");

        // Persist order
        Order order = new Order();
        order.setPatientId    (patientId);
        order.setTotalAmount  (total);
        order.setPaymentMethod(paymentStrategy.getMethodName());
        order.setStatus       (Order.Status.PENDING);
        orderDAO.save(order);

        // Persist items
        orderItemDAO.saveAll(order.getId(), items);
        order.setItems(items);

        // Deduct stock
        for (CartItem ci : cartItems) {
            Optional<Medicine> mOpt = medicineDAO.findById(ci.getMedicine().getId());
            mOpt.ifPresent(m -> {
                m.setQuantity(m.getQuantity() - ci.getQuantity());
                try { medicineDAO.update(m); } catch (SQLException ignored) {}
            });
        }

        return order;
    }

    // ── STATUS UPDATE ────────────────────────────────────────────────
    public void updateStatus(int orderId, Order.Status status) {
        try { orderDAO.updateStatus(orderId, status); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── FIND BY ID ───────────────────────────────────────────────────
    public Order findById(int orderId) {
        try {
            Optional<Order> opt = orderDAO.findById(orderId);
            if (opt.isPresent()) {
                Order o = opt.get();
                o.setItems(orderItemDAO.findByOrder(orderId));
                return o;
            }
            return null;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── FIND BY PATIENT ──────────────────────────────────────────────
    public List<Order> getPatientOrders(int patientId) {
        try { return orderDAO.findByPatient(patientId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ── FIND ALL ─────────────────────────────────────────────────────
    public List<Order> getAllOrders() {
        try { return orderDAO.findAll(); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public int getTotalOrderCount()    {
        try { return orderDAO.count(); } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public double getTotalRevenue()    {
        try { return orderDAO.totalRevenue(); } catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<OrderItem> getOrderItems(int orderId) {
        try { return orderItemDAO.findByOrder(orderId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }
}
