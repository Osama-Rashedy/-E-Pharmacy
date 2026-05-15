package com.epharmacy.patterns.strategy;

/** Concrete Strategy: Credit Card payment. */
public class CreditCardPayment implements PaymentStrategy {

    private final String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean pay(double amount) {
        // In production: integrate with a payment gateway
        System.out.printf("[Payment] Credit card ****%s charged $%.2f.%n",
                cardNumber.length() >= 4 ? cardNumber.substring(cardNumber.length() - 4) : "????",
                amount);
        return true;
    }

    @Override public String getMethodName() { return "Credit Card"; }
}
