package com.epharmacy.dao;

import com.epharmacy.database.DatabaseConnection;
import com.epharmacy.models.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Data Access Object for the notifications table. */
public class NotificationDAO {

    private Connection conn() {
        return DatabaseConnection.getInstance().getConnection();
    }

    public void save(Notification n) throws SQLException {
        String sql = "INSERT INTO notifications(user_id,message,type) VALUES(?,?,?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (n.getUserId() > 0) ps.setInt(1, n.getUserId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, n.getMessage());
            ps.setString(3, n.getType());
            ps.executeUpdate();
            var rs = ps.getGeneratedKeys();
            if (rs.next()) n.setId(rs.getInt(1));
        }
    }

    public List<Notification> findByUser(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id=? ORDER BY created_at DESC LIMIT 50";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Notification> findUnread(int userId) throws SQLException {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id=? AND is_read=false ORDER BY created_at DESC";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, userId);
            var rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public void markAllRead(int userId) throws SQLException {
        try (PreparedStatement ps = conn().prepareStatement(
                "UPDATE notifications SET is_read=true WHERE user_id=?")) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId     (rs.getInt   ("id"));
        n.setUserId (rs.getInt   ("user_id"));
        n.setMessage(rs.getString("message"));
        n.setType   (rs.getString("type"));
        n.setRead   (rs.getBoolean("is_read"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) n.setCreatedAt(ts.toLocalDateTime());
        return n;
    }
}
