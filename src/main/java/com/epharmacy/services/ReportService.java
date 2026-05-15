package com.epharmacy.services;

import com.epharmacy.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;

/** Generates chart/report data from the database. */
public class ReportService {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    /** Revenue per month (last 6 months). */
    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> data = new LinkedHashMap<>();
        String sql = """
            SELECT DATE_FORMAT(created_at, '%b %Y') AS month,
                   COALESCE(SUM(total_amount), 0)   AS revenue
            FROM orders
            WHERE status != 'CANCELLED'
              AND created_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH)
            GROUP BY DATE_FORMAT(created_at, '%b %Y'),
                     YEAR(created_at), MONTH(created_at)
            ORDER BY YEAR(created_at), MONTH(created_at)
        """;
        try {
            Statement st = conn().createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                data.put(rs.getString("month"), rs.getDouble("revenue"));
            }
        } catch (Exception e) {
            System.err.println("[ReportService] " + e.getMessage());
        }
        return data;
    }

    /** Medicine count per category. */
    public Map<String, Integer> getMedicinesByCategory() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT category, COUNT(*) AS cnt FROM medicines GROUP BY category ORDER BY cnt DESC";
        try {
            ResultSet rs = conn().createStatement().executeQuery(sql);
            while (rs.next()) {
                data.put(rs.getString("category"), rs.getInt("cnt"));
            }
        } catch (Exception e) {
            System.err.println("[ReportService] " + e.getMessage());
        }
        return data;
    }

    /** Orders per status. */
    public Map<String, Integer> getOrdersByStatus() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = "SELECT status, COUNT(*) AS cnt FROM orders GROUP BY status";
        try {
            ResultSet rs = conn().createStatement().executeQuery(sql);
            while (rs.next()) {
                data.put(rs.getString("status"), rs.getInt("cnt"));
            }
        } catch (Exception e) {
            System.err.println("[ReportService] " + e.getMessage());
        }
        return data;
    }
}
