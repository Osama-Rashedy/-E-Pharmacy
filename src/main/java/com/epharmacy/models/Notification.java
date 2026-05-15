package com.epharmacy.models;

import java.time.LocalDateTime;

/** System notification / alert for any user. */
public class Notification {

    private int           id;
    private int           userId;
    private String        message;
    private String        type;       // e.g. LOW_STOCK, EXPIRY, ORDER_UPDATE
    private boolean       read;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(int userId, String message, String type) {
        this.userId  = userId;
        this.message = message;
        this.type    = type;
    }

    // ── Getters & Setters ───────────────────────────────────────────
    public int    getId()                         { return id; }
    public void   setId(int id)                   { this.id = id; }

    public int    getUserId()                     { return userId; }
    public void   setUserId(int uid)              { this.userId = uid; }

    public String getMessage()                    { return message; }
    public void   setMessage(String message)      { this.message = message; }

    public String getType()                       { return type; }
    public void   setType(String type)            { this.type = type; }

    public boolean isRead()                       { return read; }
    public void    setRead(boolean read)          { this.read = read; }

    public LocalDateTime getCreatedAt()           { return createdAt; }
    public void          setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }
}
