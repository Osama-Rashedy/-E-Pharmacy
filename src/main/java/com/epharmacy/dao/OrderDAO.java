package com.epharmacy.dao;

import com.epharmacy.database.DatabaseConnection;
import com.epharmacy.models.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Data Access Object for the orders table. */
public class OrderDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── INSERT ──────────────────────────────────────────────────────
    public void save(Order order) throws SQLException {
        String sql = "INSERT INTO orders(patient_id,total_amount,payment_method,status) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, order.getPatientId());
            ps.setDouble(2, order.getTotalAmount());
            ps.setString(3, order.getPaymentMethod());
            ps.setString(4, order.getStatus().name());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) order.setId(rs.getInt(1));
        }
    }

    // ── UPDATE STATUS ───────────────────────────────────────────────
    public void updateStatus(int orderId, Order.Status status) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE orders SET status=? WHERE id=?")) {
            ps.setString(1, status.name());
            ps.setInt   (2, orderId);
            ps.executeUpdate();
        }
    }

    // ── FIND BY ID ──────────────────────────────────────────────────
    public Optional<Order> findById(int id) throws SQLException {
        String sql = "SELECT o.*, u.name AS patient_name FROM orders o " +
                     "JOIN users u ON o.patient_id = u.id WHERE o.id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    // ── FIND BY PATIENT ─────────────────────────────────────────────
    public List<Order> findByPatient(int patientId) throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name AS patient_name FROM orders o " +
                     "JOIN users u ON o.patient_id = u.id WHERE o.patient_id=? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, patientId);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── FIND ALL ────────────────────────────────────────────────────
    public List<Order> findAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.name AS patient_name FROM orders o " +
                     "JOIN users u ON o.patient_id = u.id ORDER BY o.created_at DESC";
        var rs = conn().createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── COUNT ────────────────────────────────────────────────────────
    public int count() throws SQLException {
        var rs = conn().createStatement().executeQuery("SELECT COUNT(*) FROM orders");
        return rs.next() ? rs.getInt(1) : 0;
    }

    // ── TOTAL REVENUE ───────────────────────────────────────────────
    public double totalRevenue() throws SQLException {
        var rs = conn().createStatement().executeQuery(
                "SELECT COALESCE(SUM(total_amount),0) FROM orders WHERE status != 'CANCELLED'");
        return rs.next() ? rs.getDouble(1) : 0;
    }

    // ── ROW MAPPER ───────────────────────────────────────────────────
    private Order mapRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId           (rs.getInt("id"));
        o.setPatientId    (rs.getInt("patient_id"));
        o.setPatientName  (rs.getString("patient_name"));
        o.setTotalAmount  (rs.getDouble("total_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setStatus       (Order.Status.valueOf(rs.getString("status")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) o.setCreatedAt(ts.toLocalDateTime());
        return o;
    }
}
