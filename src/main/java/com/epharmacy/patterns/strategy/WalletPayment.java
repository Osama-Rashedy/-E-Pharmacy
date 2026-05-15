package com.epharmacy.patterns.strategy;

/** Concrete Strategy: Digital Wallet payment. */
public class WalletPayment implements PaymentStrategy {

    private double balance;

    public WalletPayment(double balance) {
        this.balance = balance;
    }

    @Override
    public boolean pay(double amount) {
        if (balance < amount) {
            System.out.println("[Payment] Wallet: insufficient balance.");
            return false;
        }
        balance -= amount;
        System.out.printf("[Payment] Wallet charged $%.2f. Remaining balance: $%.2f%n", amount, balance);
        return true;
    }

    @Override public String getMethodName() { return "Wallet"; }

    public double getBalance() { return balance; }
}
