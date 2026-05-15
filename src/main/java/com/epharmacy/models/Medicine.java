package com.epharmacy.models;

import java.time.LocalDate;

/**
 * Medicine entity.
 *
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Builder (Creational)            ║
 * ║  Use Medicine.Builder to construct objects       ║
 * ║  with many optional fields fluently.             ║
 * ╚══════════════════════════════════════════════════╝
 */
public class Medicine {

    private int       id;
    private String    name;
    private String    category;
    private double    price;
    private int       quantity;
    private LocalDate expiryDate;
    private String    manufacturer;
    private boolean   requiresPrescription;
    private String    description;
    private String    imagePath;

    /** Private — use Builder to construct. */
    private Medicine() {}

    // ── Getters ─────────────────────────────────────────────────────
    public int       getId()                    { return id; }
    public String    getName()                  { return name; }
    public String    getCategory()              { return category; }
    public double    getPrice()                 { return price; }
    public int       getQuantity()              { return quantity; }
    public LocalDate getExpiryDate()            { return expiryDate; }
    public String    getManufacturer()          { return manufacturer; }
    public boolean   isRequiresPrescription()   { return requiresPrescription; }
    public String    getDescription()           { return description; }
    public String    getImagePath()             { return imagePath; }

    // ── Setters (for DAO mapping) ────────────────────────────────────
    public void setId(int id)                               { this.id = id; }
    public void setName(String name)                        { this.name = name; }
    public void setCategory(String category)                { this.category = category; }
    public void setPrice(double price)                      { this.price = price; }
    public void setQuantity(int quantity)                   { this.quantity = quantity; }
    public void setExpiryDate(LocalDate expiryDate)         { this.expiryDate = expiryDate; }
    public void setManufacturer(String manufacturer)        { this.manufacturer = manufacturer; }
    public void setRequiresPrescription(boolean rp)         { this.requiresPrescription = rp; }
    public void setDescription(String description)          { this.description = description; }
    public void setImagePath(String imagePath)              { this.imagePath = imagePath; }

    public boolean isLowStock()     { return quantity > 0 && quantity <= 20; }
    public boolean isOutOfStock()   { return quantity <= 0; }
    public boolean isExpiringSoon() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now().plusMonths(3));
    }

    @Override public String toString() { return name + " (" + category + ")"; }

    // ════════════════════════════════════════════════════════════════
    //  Builder — DESIGN PATTERN: Builder (Creational)
    // ════════════════════════════════════════════════════════════════
    public static class Builder {

        private final Medicine medicine = new Medicine();

        public Builder id(int id)                           { medicine.id = id;           return this; }
        public Builder name(String name)                    { medicine.name = name;       return this; }
        public Builder category(String cat)                 { medicine.category = cat;    return this; }
        public Builder price(double price)                  { medicine.price = price;     return this; }
        public Builder quantity(int qty)                    { medicine.quantity = qty;    return this; }
        public Builder expiryDate(LocalDate date)           { medicine.expiryDate = date; return this; }
        public Builder manufacturer(String mfr)             { medicine.manufacturer = mfr; return this; }
        public Builder requiresPrescription(boolean rp)     { medicine.requiresPrescription = rp; return this; }
        public Builder description(String desc)             { medicine.description = desc; return this; }
        public Builder imagePath(String path)               { medicine.imagePath = path;  return this; }

        public Medicine build() {
            if (medicine.name == null || medicine.name.isBlank())
                throw new IllegalStateException("Medicine name is required.");
            return medicine;
        }
    }
}
