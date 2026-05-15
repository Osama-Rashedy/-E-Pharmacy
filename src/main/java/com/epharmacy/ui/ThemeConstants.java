package com.epharmacy.ui;

/**
 * Design tokens mirrored in {@code themes.css}. Use for charts, PDFs, or programmatic styling.
 */
public final class ThemeConstants {

    private ThemeConstants() {}

    public static final class Light {
        public static final String ACCENT = "#0891B2";
        public static final String ACCENT_SOFT = "#E0F2FE";
        public static final String SUCCESS = "#059669";
        public static final String WARNING = "#D97706";
        public static final String DANGER = "#DC2626";
        public static final String BG_APP = "#F4F9FC";
        public static final String BG_SURFACE = "#FFFFFF";
        public static final String TEXT_PRIMARY = "#0F172A";
    }

    public static final class Dark {
        public static final String ACCENT = "#22D3EE";
        public static final String ACCENT_SOFT = "#164E63";
        public static final String SUCCESS = "#34D399";
        public static final String BG_APP = "#0B1220";
        public static final String BG_SURFACE = "#141C2B";
        public static final String TEXT_PRIMARY = "#F1F5F9";
    }

    /** Chart series — healthcare palette */
    public static final String[] CHART_COLORS = {
            "#0891B2", "#10B981", "#38BDF8", "#6366F1", "#F59E0B", "#14B8A6"
    };
}
