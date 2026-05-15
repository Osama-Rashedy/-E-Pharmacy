package com.epharmacy.utils;

import javafx.concurrent.Task;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Runs work off the JavaFX UI thread, then applies results on it. */
public final class AsyncHelper {

    private AsyncHelper() {}

    public static <T> void supplyAsync(Supplier<T> supplier, Consumer<T> onSuccess) {
        Task<T> task = new Task<>() {
            @Override protected T call() { return supplier.get(); }
        };
        task.setOnSucceeded(e -> {
            if (onSuccess != null) onSuccess.accept(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) System.err.println("[Async] " + ex.getMessage());
        });
        Thread t = new Thread(task, "epharmacy-bg");
        t.setDaemon(true);
        t.start();
    }

    public static void runAsync(Runnable background, Runnable onSuccess) {
        supplyAsync(() -> { background.run(); return null; }, v -> {
            if (onSuccess != null) onSuccess.run();
        });
    }
}
