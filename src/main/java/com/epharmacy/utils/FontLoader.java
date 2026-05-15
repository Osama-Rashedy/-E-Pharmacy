package com.epharmacy.utils;

import javafx.scene.text.Font;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Loads bundled UI fonts; falls back to system fonts when missing. */
public final class FontLoader {

    private static final Logger LOG = Logger.getLogger(FontLoader.class.getName());

    private FontLoader() {}

    public static void loadApplicationFonts() {
        load("/fonts/PlusJakartaSans-Regular.ttf", 14);
        load("/fonts/PlusJakartaSans-SemiBold.ttf", 14);
        load("/fonts/JetBrainsMono-Regular.ttf", 13);
    }

    private static void load(String resource, double size) {
        try (InputStream in = FontLoader.class.getResourceAsStream(resource)) {
            if (in != null) {
                Font.loadFont(in, size);
            }
        } catch (Exception e) {
            LOG.log(Level.FINE, "Font not loaded: " + resource, e);
        }
    }
}
