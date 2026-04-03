package com.pocketcanvascore.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

/**
 * Higher-level UI components built on top of {@link PocketCanvas}.
 */
public final class PocketComponents {
    private PocketComponents() {}

    // =====================================================================
    //  Progress bar
    // =====================================================================

    /**
     * Horizontal progress bar with a rounded-rect track and fill.
     *
     * @param context    PocketRenderContext
     * @param x          left edge
     * @param y          top edge
     * @param width      total width
     * @param height     total height
     * @param percentage 0.0-1.0 fill ratio
     * @param color      fill colour (ARGB)
     */
    public static void drawProgressBar(PocketRenderContext context, int x, int y,
                                       int width, int height,
                                       float percentage, int color) {
        if (width <= 0 || height <= 0) return;
        percentage = Math.clamp(percentage, 0f, 1f);
        PocketCanvas canvas = new PocketCanvas(context);

        int radius = Math.min(height / 2, 4);

        // Track (background)
        canvas.drawRoundedRect(x, y, width, height, radius, PocketTheme.PROGRESS_TRACK);

        // Fill
        int fillW = (int) (width * percentage);
        if (fillW > 0) {
            fillW = Math.max(fillW, radius * 2); // ensure corners render cleanly
            fillW = Math.min(fillW, width);
            canvas.drawRoundedRect(x, y, fillW, height, radius, color);
        }
    }

    /**
     * Progress bar using the default accent colour.
     */
    public static void drawProgressBar(PocketRenderContext context, int x, int y,
                                       int width, int height, float percentage) {
        drawProgressBar(context, x, y, width, height, percentage, PocketTheme.PROGRESS_FILL);
    }

    // =====================================================================
    //  Labelled progress bar
    // =====================================================================

    /**
     * Progress bar with a centred percentage label.
     */
    public static void drawLabelledProgressBar(PocketRenderContext context, int x, int y,
                                               int width, int height,
                                               float percentage, int color) {
        drawProgressBar(context, x, y, width, height, percentage, color);

        // Label
        Font font = Minecraft.getInstance().font;
        String label = String.valueOf((int) (percentage * 100)) + "%";
        int textW = font.width(label);
        int textX = x + (width - textW) / 2;
        int textY = y + (height - 8) / 2; // 8 ~ font height
        context.graphics().drawString(font, Component.literal(label), textX, textY, PocketTheme.TEXT_MAIN, true);
    }

    // =====================================================================
    //  Dark panel (shadow + rounded rect + optional border)
    // =====================================================================

    /**
     * A dark panel with optional drop shadow and border.
     */
    public static void drawPanel(PocketRenderContext context, int x, int y,
                                 int width, int height, int cornerRadius,
                                 boolean shadow, boolean border) {
        PocketCanvas canvas = new PocketCanvas(context);
        if (shadow) {
            canvas.drawDropShadow(x, y, width, height, cornerRadius);
        }
        canvas.drawRoundedRect(x, y, width, height, cornerRadius, PocketTheme.BACKGROUND_PANEL);
        if (border) {
            canvas.drawRoundedBorder(x, y, width, height, cornerRadius, PocketTheme.BORDER_DEFAULT);
        }
    }

    /**
     * Convenience overload - shadow on, border on.
     */
    public static void drawPanel(PocketRenderContext context, int x, int y,
                                 int width, int height, int cornerRadius) {
        drawPanel(context, x, y, width, height, cornerRadius, true, true);
    }

    // =====================================================================
    //  Circular progress ring
    // =====================================================================

    /**
     * Circular progress ring built on {@link PocketCanvas#drawAnnulusArc}.
     *
     * @param cx         centre X
     * @param cy         centre Y
     * @param radius     outer radius
     * @param thickness  ring thickness
     * @param percentage 0.0-1.0
     * @param trackColor background ring colour
     * @param fillColor  filled-arc colour
     */
    public static void drawProgressRing(PocketRenderContext context, int cx, int cy,
                                        int radius, int thickness,
                                        float percentage,
                                        int trackColor, int fillColor) {
        percentage = Math.clamp(percentage, 0f, 1f);
        PocketCanvas canvas = new PocketCanvas(context);

        // Full track ring
        canvas.drawAnnulus(cx, cy, radius, thickness, trackColor, trackColor);

        // Progress arc (starts at 12 o'clock = -PI/2)
        if (percentage > 0f) {
            float startAngle = (float) (-Math.PI / 2);
            float endAngle = startAngle + (float) (2.0 * Math.PI * percentage);
            canvas.drawAnnulusArc(cx, cy, radius, thickness,
                    startAngle, endAngle, fillColor, fillColor);
        }
    }

    /**
     * Progress ring with default theme colours.
     */
    public static void drawProgressRing(PocketRenderContext context, int cx, int cy,
                                        int radius, int thickness, float percentage) {
        drawProgressRing(context, cx, cy, radius, thickness, percentage,
                PocketTheme.PROGRESS_TRACK, PocketTheme.PROGRESS_FILL);
    }

    // =====================================================================
    //  Tooltip panel
    // =====================================================================

    /**
     * A compact tooltip panel (dark elevated surface with text).
     * Draws as a rounded rect with a 1-pixel accent border.
     */
    public static void drawTooltipPanel(PocketRenderContext context, int x, int y,
                                        int width, int height) {
        PocketCanvas canvas = new PocketCanvas(context);
        canvas.drawDropShadow(x, y, width, height, 4, 4, 80);
        canvas.drawRoundedRect(x, y, width, height, 4, PocketTheme.BACKGROUND_ELEVATED);
        canvas.drawRoundedBorder(x, y, width, height, 4, PocketTheme.BORDER_DEFAULT);
    }

    // =====================================================================
    //  Separator / divider
    // =====================================================================

    /** Themed horizontal divider. */
    public static void drawDivider(PocketRenderContext context, int x, int y, int width) {
        PocketCanvas canvas = new PocketCanvas(context);
        canvas.drawDivider(x, y, width, PocketTheme.DIVIDER);
    }
}
