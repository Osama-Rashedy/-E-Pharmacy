package com.epharmacy.patterns.strategy;

/** Concrete Strategy: Cash payment. */
public class CashPayment implements PaymentStrategy {

    @Override
    public boolean pay(double amount) {
        System.out.printf("[Payment] Cash payment of $%.2f processed.%n", amount);
        return true;  // Cash always succeeds in this simulation
    }

    @Override public String getMethodName() { return "Cash"; }
}
