package com.epharmacy.dao;

import com.epharmacy.database.DatabaseConnection;
import com.epharmacy.models.Prescription;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Data Access Object for the prescriptions table. */
public class PrescriptionDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── INSERT ──────────────────────────────────────────────────────
    public void save(Prescription p) throws SQLException {
        String sql = "INSERT INTO prescriptions(patient_id,order_id,image_path,status,notes) VALUES(?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt   (1, p.getPatientId());
            if (p.getOrderId() != null) ps.setInt(2, p.getOrderId()); else ps.setNull(2, Types.INTEGER);
            ps.setString(3, p.getImagePath());
            ps.setString(4, p.getStatus().name());
            ps.setString(5, p.getNotes());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) p.setId(rs.getInt(1));
        }
    }

    // ── UPDATE STATUS ───────────────────────────────────────────────
    public void updateStatus(int id, Prescription.Status status, String notes) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE prescriptions SET status=?, notes=? WHERE id=?")) {
            ps.setString(1, status.name());
            ps.setString(2, notes);
            ps.setInt   (3, id);
            ps.executeUpdate();
        }
    }

    // ── FIND PENDING ────────────────────────────────────────────────
    public List<Prescription> findPending() throws SQLException {
        return findByStatus(Prescription.Status.PENDING);
    }

    public List<Prescription> findByStatus(Prescription.Status status) throws SQLException {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT p.*, u.name AS patient_name FROM prescriptions p " +
                     "JOIN users u ON p.patient_id = u.id WHERE p.status=? ORDER BY p.uploaded_at DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, status.name());
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── FIND ALL ────────────────────────────────────────────────────
    public List<Prescription> findAll() throws SQLException {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT p.*, u.name AS patient_name FROM prescriptions p " +
                     "JOIN users u ON p.patient_id = u.id ORDER BY p.uploaded_at DESC";
        var rs = conn().createStatement().executeQuery(sql);
        while (rs.next()) list.add(mapRow(rs));
        return list;
    }

    // ── FIND BY PATIENT ─────────────────────────────────────────────
    public List<Prescription> findByPatient(int patientId) throws SQLException {
        List<Prescription> list = new ArrayList<>();
        String sql = "SELECT p.*, u.name AS patient_name FROM prescriptions p " +
                     "JOIN users u ON p.patient_id = u.id WHERE p.patient_id=? ORDER BY p.uploaded_at DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, patientId);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── ROW MAPPER ───────────────────────────────────────────────────
    private Prescription mapRow(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setId         (rs.getInt("id"));
        p.setPatientId  (rs.getInt("patient_id"));
        p.setPatientName(rs.getString("patient_name"));
        int oid = rs.getInt("order_id");
        p.setOrderId(rs.wasNull() ? null : oid);
        p.setImagePath  (rs.getString("image_path"));
        p.setStatus     (Prescription.Status.valueOf(rs.getString("status")));
        p.setNotes      (rs.getString("notes"));
        Timestamp ts = rs.getTimestamp("uploaded_at");
        if (ts != null) p.setUploadedAt(ts.toLocalDateTime());
        return p;
    }
}
