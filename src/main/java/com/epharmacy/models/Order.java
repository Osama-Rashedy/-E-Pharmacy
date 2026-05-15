package com.epharmacy.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Represents a patient's purchase order. */
public class Order {

    public enum Status { PENDING, APPROVED, DELIVERED, CANCELLED }

    private int           id;
    private int           patientId;
    private String        patientName;   // display only
    private double        totalAmount;
    private String        paymentMethod;
    private Status        status;
    private LocalDateTime createdAt;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {}

    // ── Getters & Setters ───────────────────────────────────────────
    public int    getId()                           { return id; }
    public void   setId(int id)                     { this.id = id; }

    public int    getPatientId()                    { return patientId; }
    public void   setPatientId(int pid)             { this.patientId = pid; }

    public String getPatientName()                  { return patientName; }
    public void   setPatientName(String name)       { this.patientName = name; }

    public double getTotalAmount()                  { return totalAmount; }
    public void   setTotalAmount(double total)      { this.totalAmount = total; }

    public String getPaymentMethod()                { return paymentMethod; }
    public void   setPaymentMethod(String pm)       { this.paymentMethod = pm; }

    public Status getStatus()                       { return status; }
    public void   setStatus(Status status)          { this.status = status; }

    public LocalDateTime getCreatedAt()             { return createdAt; }
    public void          setCreatedAt(LocalDateTime dt) { this.createdAt = dt; }

    public List<OrderItem> getItems()               { return items; }
    public void            setItems(List<OrderItem> items) { this.items = items; }
}
