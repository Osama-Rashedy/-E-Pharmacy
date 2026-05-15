package com.epharmacy.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/**
 * Reusable UI building blocks (empty states, toolbars, loading overlay).
 */
public final class UiComponents {

    private UiComponents() {}

    public static VBox emptyState(String title, String subtitle) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("empty-state");
        box.getChildren().addAll(
                AppIcons.stat(FontAwesomeSolid.INBOX, "empty-state-icon"),
                textLabel(title, "empty-state-title"),
                textLabel(subtitle, "empty-state-subtitle")
        );
        return box;
    }

    public static void bindTablePlaceholder(TableView<?> table, String title, String subtitle) {
        if (table == null) return;
        table.setPlaceholder(emptyState(title, subtitle));
        if (!table.getStyleClass().contains("table-modern")) {
            table.getStyleClass().add("table-modern");
        }
    }

    public static HBox searchToolbar(TextField field, String prompt, Runnable onSearch) {
        field.setPromptText(prompt);
        field.getStyleClass().add("toolbar-search");
        field.setMaxWidth(420);
        HBox.setHgrow(field, javafx.scene.layout.Priority.ALWAYS);

        Button searchBtn = new Button("Search");
        searchBtn.getStyleClass().addAll("button", "button-sm");
        searchBtn.setGraphic(AppIcons.action(FontAwesomeSolid.SEARCH));
        searchBtn.setOnAction(e -> {
            if (onSearch != null) onSearch.run();
        });
        field.setOnAction(e -> {
            if (onSearch != null) onSearch.run();
        });

        HBox bar = new HBox(12, field, searchBtn);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("toolbar");
        return bar;
    }

    public static StackPane loadingOverlay(Node content) {
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.getStyleClass().add("loading-spinner");
        spinner.setMaxSize(48, 48);
        Label label = textLabel("Loading…", "loading-label");
        VBox loadBox = new VBox(12, spinner, label);
        loadBox.setAlignment(Pos.CENTER);
        loadBox.getStyleClass().add("loading-overlay");

        StackPane stack = new StackPane(content, loadBox);
        loadBox.setVisible(false);
        loadBox.setManaged(false);
        stack.getProperties().put("loadingNode", loadBox);
        return stack;
    }

    public static void setLoading(StackPane stack, boolean loading) {
        if (stack == null) return;
        Object node = stack.getProperties().get("loadingNode");
        if (node instanceof javafx.scene.Node n) {
            n.setVisible(loading);
            n.setManaged(loading);
        }
    }

    public static Button iconButton(String text, FontAwesomeSolid icon, String... styleClasses) {
        Button b = new Button(text);
        b.setGraphic(AppIcons.action(icon));
        b.getStyleClass().add("button");
        if (styleClasses != null) {
            b.getStyleClass().addAll(styleClasses);
        }
        return b;
    }

    private static Label textLabel(String text, String style) {
        Label l = new Label(text);
        if (style != null) l.getStyleClass().add(style);
        return l;
    }
}
