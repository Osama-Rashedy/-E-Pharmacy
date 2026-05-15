package com.epharmacy.patterns.strategy;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Strategy (Behavioral)           ║
 * ║  Allows swapping payment algorithms at runtime   ║
 * ║  without changing the checkout logic.            ║
 * ╚══════════════════════════════════════════════════╝
 */
public interface PaymentStrategy {

    /**
     * Processes the payment.
     * @param amount total amount to charge
     * @return true if payment succeeded
     */
    boolean pay(double amount);

    /** Human-readable name of this method. */
    String  getMethodName();
}
