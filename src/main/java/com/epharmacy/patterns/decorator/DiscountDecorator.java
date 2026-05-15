package com.epharmacy.patterns.decorator;

/** Decorator: applies a flat percentage discount to the invoice. */
public class DiscountDecorator extends InvoiceDecorator {

    private final double discountRate;  // e.g. 0.10 for 10%

    public DiscountDecorator(Invoice wrapped, double discountRate) {
        super(wrapped);
        this.discountRate = discountRate;
    }

    @Override
    public double getTotal() {
        return wrapped.getTotal() * (1 - discountRate);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription()
                + String.format("%n  - Discount (%.0f%%): -$%.2f",
                discountRate * 100, wrapped.getTotal() * discountRate);
    }
}
