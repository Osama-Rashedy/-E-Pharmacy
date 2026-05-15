package com.epharmacy.models;

/** Single line item within an order. */
public class OrderItem {

    private int    id;
    private int    orderId;
    private int    medicineId;
    private String medicineName;   // display only
    private int    quantity;
    private double unitPrice;

    public OrderItem() {}

    public OrderItem(int medicineId, String medicineName, int quantity, double unitPrice) {
        this.medicineId   = medicineId;
        this.medicineName = medicineName;
        this.quantity     = quantity;
        this.unitPrice    = unitPrice;
    }

    // ── Getters & Setters ──────────────────────────────────────────
    public int getId()                          { return id; }
    public void setId(int id)                   { this.id = id; }

    public int getOrderId()                     { return orderId; }
    public void setOrderId(int orderId)         { this.orderId = orderId; }

    public int getMedicineId()                  { return medicineId; }
    public void setMedicineId(int mid)          { this.medicineId = mid; }

    public String getMedicineName()             { return medicineName; }
    public void setMedicineName(String name)    { this.medicineName = name; }

    public int getQuantity()                    { return quantity; }
    public void setQuantity(int quantity)       { this.quantity = quantity; }

    public double getUnitPrice()                { return unitPrice; }
    public void setUnitPrice(double unitPrice)  { this.unitPrice = unitPrice; }

    public double getSubtotal() { return quantity * unitPrice; }
}
