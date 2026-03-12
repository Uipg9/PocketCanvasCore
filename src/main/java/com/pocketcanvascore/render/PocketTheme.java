package com.pocketcanvascore.render;

/**
 * Centralized dark-mode colour palette for PocketCanvasCore.
 * All values are ARGB (0xAARRGGBB).
 */
public final class PocketTheme {
    private PocketTheme() {}

    // ── Backgrounds ─────────────────────────────────────────
    /** Deep near-black for full-screen overlays and root backgrounds. */
    public static final int BACKGROUND_MAIN      = 0xFF0D0D0D;
    /** Slightly lighter grey for panels and cards. */
    public static final int BACKGROUND_PANEL     = 0xFF1A1A1A;
    /** Elevated surface (modals, dropdowns, popovers). */
    public static final int BACKGROUND_ELEVATED  = 0xFF242424;
    /** Input fields, wells, inset areas. */
    public static final int BACKGROUND_INPUT     = 0xFF141414;

    // ── Accent colours ──────────────────────────────────────
    /** Primary accent — vibrant teal-green. */
    public static final int ACCENT_PRIMARY       = 0xFF00C896;
    /** Secondary accent — blue. */
    public static final int ACCENT_SECONDARY     = 0xFF3B82F6;
    /** Warning amber. */
    public static final int ACCENT_WARNING       = 0xFFF59E0B;
    /** Error / destructive red. */
    public static final int ACCENT_ERROR         = 0xFFEF4444;
    /** Positive / success green. */
    public static final int ACCENT_SUCCESS       = 0xFF22C55E;

    // ── Text ────────────────────────────────────────────────
    /** Primary readable text — near-white. */
    public static final int TEXT_MAIN            = 0xFFECECEC;
    /** Secondary / muted text. */
    public static final int TEXT_MUTED           = 0xFF808080;
    /** Disabled / placeholder text. */
    public static final int TEXT_DISABLED        = 0xFF4A4A4A;

    // ── Borders & surfaces ──────────────────────────────────
    /** Subtle border colour. */
    public static final int BORDER_DEFAULT       = 0xFF2A2A2A;
    /** Accent-coloured border. */
    public static final int BORDER_ACCENT        = 0xFF00C896;
    /** Thin divider line. */
    public static final int DIVIDER              = 0xFF1F1F1F;
    /** Semi-transparent overlay (75% black). */
    public static final int OVERLAY              = 0xC0000000;
    /** Shadow base (pure black, variable alpha). */
    public static final int SHADOW               = 0x40000000;

    // ── Gameplay / icon palette ─────────────────────────────
    /** Villager skin tone. */
    public static final int VILLAGER_SKIN        = 0xFFC4A882;
    /** Villager brown robe. */
    public static final int VILLAGER_ROBE        = 0xFF6B4226;
    /** Villager green eyes. */
    public static final int VILLAGER_EYES        = 0xFF3D8B37;
    /** Villager nose highlight. */
    public static final int VILLAGER_NOSE        = 0xFFB89B72;
    /** Farmland dirt brown. */
    public static final int CROP_DIRT            = 0xFF6B4423;
    /** Green stalk. */
    public static final int CROP_STALK           = 0xFF4CAF50;
    /** Light-green stalk tip. */
    public static final int CROP_TIP             = 0xFF81C784;
    /** Ripe golden wheat head. */
    public static final int CROP_WHEAT           = 0xFFDAA520;

    // ── Progress-bar palette ────────────────────────────────
    /** Default progress bar foreground. */
    public static final int PROGRESS_FILL        = ACCENT_PRIMARY;
    /** Progress bar background track. */
    public static final int PROGRESS_TRACK       = 0xFF2A2A2A;
}
