package com.epharmacy.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Singleton (Creational)          ║
 * ║  Guarantees a single database connection         ║
 * ║  instance across the entire application.         ║
 * ╚══════════════════════════════════════════════════╝
 *
 * Thread-safe using double-checked locking.
 */
public class DatabaseConnection {

    // ── The one and only instance ────────────────────────────────────
    private static volatile DatabaseConnection instance;
    private Connection connection;

    // ── DB credentials loaded from config.properties ─────────────────
    private static String url;
    private static String username;
    private static String password;

    static {
        loadConfig();
    }

    /** Private constructor — no one can call new DatabaseConnection() */
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to the database: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the singleton instance (double-checked locking).
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /** Returns the active JDBC Connection, reconnecting if closed. */
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, username, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot retrieve DB connection: " + e.getMessage(), e);
        }
        return connection;
    }

    // ── Load DB config from resources/config.properties ──────────────
    private static void loadConfig() {
        Properties props = new Properties();
        try (InputStream in = DatabaseConnection.class
                .getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("config.properties not found, using defaults.");
        }
        url      = props.getProperty("db.url",      "jdbc:mysql://localhost:3306/epharmacy?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC");
        username = props.getProperty("db.username",  "root");
        password = props.getProperty("db.password",  "");
    }
}
