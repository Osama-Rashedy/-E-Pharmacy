package com.epharmacy.dao;

import com.epharmacy.database.DatabaseConnection;
import com.epharmacy.models.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for the order_items table. */
public class OrderItemDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── BATCH INSERT ─────────────────────────────────────────────────
    public void saveAll(int orderId, List<OrderItem> items) throws SQLException {
        String sql = "INSERT INTO order_items(order_id,medicine_id,quantity,unit_price) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            for (OrderItem item : items) {
                ps.setInt   (1, orderId);
                ps.setInt   (2, item.getMedicineId());
                ps.setInt   (3, item.getQuantity());
                ps.setDouble(4, item.getUnitPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    // ── FIND BY ORDER ────────────────────────────────────────────────
    public List<OrderItem> findByOrder(int orderId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.*, m.name AS medicine_name FROM order_items oi " +
                     "JOIN medicines m ON oi.medicine_id = m.id WHERE oi.order_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, orderId);
            var rs = ps.executeQuery();
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt   ("medicine_id"),
                        rs.getString("medicine_name"),
                        rs.getInt   ("quantity"),
                        rs.getDouble("unit_price")
                );
                item.setId(rs.getInt("id"));
                item.setOrderId(orderId);
                list.add(item);
            }
        }
        return list;
    }
}
