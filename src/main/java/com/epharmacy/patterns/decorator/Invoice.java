package com.epharmacy.patterns.decorator;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Decorator (Structural)          ║
 * ║  Dynamically enhances invoice objects with       ║
 * ║  tax, discount, or loyalty features at runtime   ║
 * ║  without altering the base Invoice class.        ║
 * ╚══════════════════════════════════════════════════╝
 *
 * Component interface — all invoice types implement this.
 */
public interface Invoice {

    /** Returns the total cost after all decorations are applied. */
    double getTotal();

    /** Human-readable description of what was applied. */
    String getDescription();
}
