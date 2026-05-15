package com.epharmacy.patterns.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Command Invoker — executes commands and maintains a history
 * stack for undo operations.
 */
public class CommandInvoker {

    private final Deque<OrderCommand> history = new ArrayDeque<>();

    /** Execute the command and push it to the undo stack. */
    public void execute(OrderCommand command) {
        command.execute();
        history.push(command);
    }

    /** Undo the last executed command. */
    public void undoLast() {
        if (!history.isEmpty()) {
            history.pop().undo();
        } else {
            System.out.println("[CommandInvoker] Nothing to undo.");
        }
    }

    public boolean canUndo() { return !history.isEmpty(); }
}
