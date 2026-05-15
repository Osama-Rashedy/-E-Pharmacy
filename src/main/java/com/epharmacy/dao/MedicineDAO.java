package com.epharmacy.dao;

import com.epharmacy.database.DatabaseConnection;
import com.epharmacy.models.Medicine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Data Access Object for the medicines table. */
public class MedicineDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── INSERT ──────────────────────────────────────────────────────
    public void save(Medicine m) throws SQLException {
        String sql = "INSERT INTO medicines(name,category,price,quantity,expiry_date," +
                     "manufacturer,requires_prescription,description,image_path) VALUES(?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindMedicine(ps, m);
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) m.setId(rs.getInt(1));
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────
    public void update(Medicine m) throws SQLException {
        String sql = "UPDATE medicines SET name=?,category=?,price=?,quantity=?,expiry_date=?," +
                     "manufacturer=?,requires_prescription=?,description=?,image_path=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            bindMedicine(ps, m);
            ps.setInt(10, m.getId());
            ps.executeUpdate();
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM medicines WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── FIND ALL ────────────────────────────────────────────────────
    public List<Medicine> findAll() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        var rs = conn().createStatement().executeQuery(
                "SELECT * FROM medicines ORDER BY name");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── FIND BY ID ──────────────────────────────────────────────────
    public Optional<Medicine> findById(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM medicines WHERE id=?")) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    // ── SEARCH ──────────────────────────────────────────────────────
    public List<Medicine> search(String keyword) throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE name LIKE ? OR category LIKE ? OR manufacturer LIKE ?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw); ps.setString(2, kw); ps.setString(3, kw);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── LOW STOCK (qty ≤ 20) ────────────────────────────────────────
    public List<Medicine> findLowStock() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        var rs = conn().createStatement().executeQuery(
                "SELECT * FROM medicines WHERE quantity <= 20 AND quantity > 0 ORDER BY quantity");
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── EXPIRING IN NEXT 3 MONTHS ───────────────────────────────────
    public List<Medicine> findExpiringSoon() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT * FROM medicines WHERE expiry_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 3 MONTH)";
        var rs = conn().createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── COUNT ────────────────────────────────────────────────────────
    public int count() throws SQLException {
        var rs = conn().createStatement().executeQuery("SELECT COUNT(*) FROM medicines");
        return rs.next() ? rs.getInt(1) : 0;
    }

    // ── BIND HELPER ──────────────────────────────────────────────────
    private void bindMedicine(PreparedStatement ps, Medicine m) throws SQLException {
        ps.setString (1, m.getName());
        ps.setString (2, m.getCategory());
        ps.setDouble (3, m.getPrice());
        ps.setInt    (4, m.getQuantity());
        ps.setDate   (5, m.getExpiryDate() != null ? Date.valueOf(m.getExpiryDate()) : null);
        ps.setString (6, m.getManufacturer());
        ps.setBoolean(7, m.isRequiresPrescription());
        ps.setString (8, m.getDescription());
        ps.setString (9, m.getImagePath());
    }

    // ── ROW MAPPER ───────────────────────────────────────────────────
    public static Medicine mapRow(ResultSet rs) throws SQLException {
        Date expDate = rs.getDate("expiry_date");
        return new Medicine.Builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .category(rs.getString("category"))
                .price(rs.getDouble("price"))
                .quantity(rs.getInt("quantity"))
                .expiryDate(expDate != null ? expDate.toLocalDate() : null)
                .manufacturer(rs.getString("manufacturer"))
                .requiresPrescription(rs.getBoolean("requires_prescription"))
                .description(rs.getString("description"))
                .imagePath(rs.getString("image_path"))
                .build();
    }
}
