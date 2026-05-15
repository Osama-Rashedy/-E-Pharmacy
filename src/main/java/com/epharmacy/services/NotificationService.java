package com.epharmacy.services;

import com.epharmacy.dao.NotificationDAO;
import com.epharmacy.models.Notification;

import java.sql.SQLException;
import java.util.List;

/** Service for creating and fetching user notifications. */
public class NotificationService {

    private final NotificationDAO dao = new NotificationDAO();

    public void create(int userId, String message, String type) {
        try {
            dao.save(new Notification(userId, message, type));
        } catch (SQLException e) {
            System.err.println("[NotificationService] Failed to save notification: " + e.getMessage());
        }
    }

    /** Broadcast to all users (userId = 0 means system-wide). */
    public void broadcast(String message, String type) {
        create(0, message, type);
    }

    public List<Notification> getForUser(int userId) {
        try { return dao.findByUser(userId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public List<Notification> getUnread(int userId) {
        try { return dao.findUnread(userId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }

    public void markAllRead(int userId) {
        try { dao.markAllRead(userId); }
        catch (SQLException e) { throw new RuntimeException(e); }
    }
}
