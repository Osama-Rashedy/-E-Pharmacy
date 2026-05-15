package com.epharmacy.patterns.command;

/**
 * ╔══════════════════════════════════════════════════╗
 * ║  DESIGN PATTERN: Command (Behavioral)            ║
 * ║  Encapsulates order actions as objects so they   ║
 * ║  can be queued, logged, and undone.              ║
 * ╚══════════════════════════════════════════════════╝
 */
public interface OrderCommand {
    void execute();
    void undo();
}
