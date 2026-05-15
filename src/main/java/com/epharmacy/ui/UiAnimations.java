package com.epharmacy.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.List;

/** Lightweight transitions tuned for long desktop sessions (subtle, fast). */
public final class UiAnimations {

    private static final Duration PAGE = Duration.millis(220);
    private static final Duration STAGGER = Duration.millis(45);
    private static final Duration HOVER = Duration.millis(120);

    private UiAnimations() {}

    public static FadeTransition fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(PAGE, node);
        ft.setFromValue(0);
        ft.setToValue(1);
        return ft;
    }

    public static void staggerFadeIn(List<? extends Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return;
        int i = 0;
        for (Node n : nodes) {
            if (n == null) continue;
            n.setOpacity(0);
            FadeTransition ft = new FadeTransition(PAGE, n);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setDelay(STAGGER.multiply(i++));
            ft.play();
        }
    }

    public static void slideUpFadeIn(Node node) {
        if (node == null) return;
        node.setOpacity(0);
        node.setTranslateY(12);
        FadeTransition fade = new FadeTransition(PAGE, node);
        fade.setFromValue(0);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(PAGE, node);
        slide.setFromY(12);
        slide.setToY(0);
        ParallelTransition pt = new ParallelTransition(fade, slide);
        pt.play();
    }

    public static void hoverLift(Node node, boolean enter) {
        TranslateTransition tt = new TranslateTransition(HOVER, node);
        tt.setToY(enter ? -2 : 0);
        tt.play();
    }
}
