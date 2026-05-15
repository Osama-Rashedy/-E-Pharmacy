package com.epharmacy.models;

/** An in-memory shopping cart item (not persisted to DB directly). */
public class CartItem {

    private Medicine medicine;
    private int      quantity;

    public CartItem(Medicine medicine, int quantity) {
        this.medicine = medicine;
        this.quantity = quantity;
    }

    public Medicine getMedicine()              { return medicine; }
    public void     setMedicine(Medicine m)    { this.medicine = m; }

    public int      getQuantity()              { return quantity; }
    public void     setQuantity(int quantity)  { this.quantity = quantity; }

    public double   getSubtotal()              { return medicine.getPrice() * quantity; }

    /** Name column for TableView binding. */
    public String   getMedicineName()          { return medicine.getName(); }

    /** Price column for TableView binding. */
    public double   getUnitPrice()             { return medicine.getPrice(); }
}
