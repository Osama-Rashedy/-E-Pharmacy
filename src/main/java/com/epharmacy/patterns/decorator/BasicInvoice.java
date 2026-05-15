package com.epharmacy.patterns.decorator;

/** Concrete base invoice — wraps a raw subtotal amount. */
public class BasicInvoice implements Invoice {

    private final double subtotal;
    private final String label;

    public BasicInvoice(double subtotal, String label) {
        this.subtotal = subtotal;
        this.label    = label;
    }

    @Override public double getTotal()       { return subtotal; }
    @Override public String getDescription() { return label + ": $" + String.format("%.2f", subtotal); }
}
