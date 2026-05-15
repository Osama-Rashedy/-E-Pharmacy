package com.epharmacy.ui;

import javafx.scene.layout.StackPane;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;

/** Mounts icons into stat-card FXML placeholders. */
public final class StatCardUi {

    private StatCardUi() {}

    public static void mountIcon(StackPane host, FontAwesomeSolid icon) {
        if (host == null || icon == null) return;
        host.getChildren().setAll(AppIcons.stat(icon, null));
    }
}
