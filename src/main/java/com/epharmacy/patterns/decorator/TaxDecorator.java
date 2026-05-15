package com.epharmacy.patterns.decorator;

/** Decorator: adds VAT tax to the invoice total. */
public class TaxDecorator extends InvoiceDecorator {

    private final double taxRate;  // e.g. 0.14 for 14%

    public TaxDecorator(Invoice wrapped, double taxRate) {
        super(wrapped);
        this.taxRate = taxRate;
    }

    @Override
    public double getTotal() {
        return wrapped.getTotal() * (1 + taxRate);
    }

    @Override
    public String getDescription() {
        return wrapped.getDescription()
                + String.format("%n  + Tax (%.0f%%): $%.2f", taxRate * 100, wrapped.getTotal() * taxRate);
    }
}
