package com.epharmacy.patterns.decorator;

/** Abstract base decorator — delegates to wrapped Invoice. */
public abstract class InvoiceDecorator implements Invoice {

    protected final Invoice wrapped;

    protected InvoiceDecorator(Invoice wrapped) {
        this.wrapped = wrapped;
    }

    @Override public double getTotal()       { return wrapped.getTotal(); }
    @Override public String getDescription() { return wrapped.getDescription(); }
}
