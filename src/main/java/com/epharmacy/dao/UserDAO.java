package com.epharmacy.dao;

import com.epharmacy.database.DatabaseConnection;
import com.epharmacy.models.User;
import com.epharmacy.patterns.factory.UserFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Data Access Object for the users table. Uses PreparedStatement throughout. */
public class UserDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    // ── INSERT ──────────────────────────────────────────────────────
    public void save(User user) throws SQLException {
        String sql = "INSERT INTO users(name,email,password,role,phone,address) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole().name());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getAddress());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) user.setId(rs.getInt(1));
        }
    }

    // ── UPDATE ──────────────────────────────────────────────────────
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name=?,email=?,phone=?,address=? WHERE id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getAddress());
            ps.setInt   (5, user.getId());
            ps.executeUpdate();
        }
    }

    // ── DELETE ──────────────────────────────────────────────────────
    public void delete(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ── FIND BY EMAIL ───────────────────────────────────────────────
    public Optional<User> findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, email);
            var rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    // ── FIND BY ID ──────────────────────────────────────────────────
    public Optional<User> findById(int id) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1, id);
            var rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        }
        return Optional.empty();
    }

    // ── FIND ALL BY ROLE ────────────────────────────────────────────
    public List<User> findByRole(User.Role role) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role=? ORDER BY name";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, role.name());
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    // ── COUNT BY ROLE ───────────────────────────────────────────────
    public int countByRole(User.Role role) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "SELECT COUNT(*) FROM users WHERE role=?")) {
            ps.setString(1, role.name());
            var rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    // ── MAPPER ──────────────────────────────────────────────────────
    private User mapRow(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("role");
        User   user    = UserFactory.createUser(roleStr, "", "", "");
        user.setId     (rs.getInt   ("id"));
        user.setName   (rs.getString("name"));
        user.setEmail  (rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone  (rs.getString("phone"));
        user.setAddress(rs.getString("address"));
        return user;
    }
}
