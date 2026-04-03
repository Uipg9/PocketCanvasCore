package com.pocketcanvascore.render;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Primitive 2D drawing API backed by PocketRenderContext adapters.
 * <p>
 * Wraps version-specific GUI graphics into simple one-line methods. All colours
 * are standard ARGB ({@code 0xAARRGGBB}).
 * <p>
 * Every shape is rendered via the scanline-fill approach using
 * {@code graphics.fill}, ensuring full compatibility with the active
 * runtime rendering pipeline.
 */
public final class PocketCanvas {
    private final PocketRenderContext ctx;

    public PocketCanvas(PocketRenderContext ctx) {
        this.ctx = Objects.requireNonNull(ctx, "ctx");
    }

    public PocketRenderContext context() {
        return ctx;
    }

    public float delta() {
        return ctx.delta();
    }

    // =====================================================================
    //  Scanline geometry cache
    // =====================================================================

    private static final int CACHE_MAX = 64;
    private static final Map<Integer, int[]> SCANLINE_CACHE =
            new LinkedHashMap<>(32, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Integer, int[]> eldest) {
                    return size() > CACHE_MAX;
                }
            };

    private static int[] getScanlineDx(int radius) {
        return SCANLINE_CACHE.computeIfAbsent(radius, r -> {
            int[] dx = new int[r];
            for (int i = 0; i < r; i++) {
                int dy = r - i;
                dx[i] = (int) Math.floor(Math.sqrt((double) r * r - (double) dy * dy));
            }
            return dx;
        });
    }

    /** Clear the scanline cache (rarely needed). */
    public static void clearScanlineCache() {
        SCANLINE_CACHE.clear();
    }

    // =====================================================================
    //  Colour helpers
    // =====================================================================

    /** Replace the alpha channel of {@code color} with {@code alpha} (0–255). */
    public static int withAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | ((alpha & 0xFF) << 24);
    }

    /** Linearly interpolate between two ARGB colours. */
    public static int lerpColor(int from, int to, float t) {
        t = Math.clamp(t, 0f, 1f);
        int aF = (from >> 24) & 0xFF, rF = (from >> 16) & 0xFF, gF = (from >> 8) & 0xFF, bF = from & 0xFF;
        int aT = (to   >> 24) & 0xFF, rT = (to   >> 16) & 0xFF, gT = (to   >> 8) & 0xFF, bT = to   & 0xFF;
        int a = (int) (aF + (aT - aF) * t);
        int r = (int) (rF + (rT - rF) * t);
        int g = (int) (gF + (gT - gF) * t);
        int b = (int) (bF + (bT - bF) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Darken a colour by multiplying RGB channels. factor 0 = black, 1 = original. */
    public static int darken(int argb, float factor) {
        factor = Math.clamp(factor, 0f, 1f);
        int a = (argb >> 24) & 0xFF;
        int r = (int) (((argb >> 16) & 0xFF) * factor);
        int g = (int) (((argb >>  8) & 0xFF) * factor);
        int b = (int) ((argb & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Lighten a colour by blending toward white. factor 0 = original, 1 = white. */
    public static int lighten(int argb, float factor) {
        factor = Math.clamp(factor, 0f, 1f);
        int a = (argb >> 24) & 0xFF;
        int r = (int) (((argb >> 16) & 0xFF) + (255 - ((argb >> 16) & 0xFF)) * factor);
        int g = (int) (((argb >>  8) & 0xFF) + (255 - ((argb >>  8) & 0xFF)) * factor);
        int b = (int) ((argb & 0xFF) + (255 - (argb & 0xFF)) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // =====================================================================
    //  Filled rectangle
    // =====================================================================

    /**
     * Solid-colour filled rectangle.
     *
     * @param x       left edge
     * @param y       top edge
     * @param width   width in pixels
     * @param height  height in pixels
     * @param color   ARGB colour
     */
    public void drawRect(int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) return;
        ctx.graphics().fill(x, y, x + width, y + height, color);
    }

    // =====================================================================
    //  Rounded rectangle (scanline arc)
    // =====================================================================

    /**
     * Filled rectangle with rounded corners.
     * Uses the scanline-arc approach — three body rectangles plus
     * four quarter-circle fans drawn as single-pixel-row fills.
     *
     * @param cornerRadius corner radius in pixels (clamped to half of min dimension)
     */
    public void drawRoundedRect(int x, int y,
                                int width, int height,
                                int cornerRadius, int color) {
        if (width <= 0 || height <= 0) return;
        cornerRadius = Math.min(cornerRadius, Math.min(width, height) / 2);
        if (cornerRadius <= 0) {
            drawRect(x, y, width, height, color);
            return;
        }

        // Body rectangles (no overlap)
        ctx.graphics().fill(x + cornerRadius, y, x + width - cornerRadius, y + height, color);
        ctx.graphics().fill(x, y + cornerRadius, x + cornerRadius, y + height - cornerRadius, color);
        ctx.graphics().fill(x + width - cornerRadius, y + cornerRadius, x + width, y + height - cornerRadius, color);

        // Corner scanlines
        int[] dxArr = getScanlineDx(cornerRadius);
        for (int i = 0; i < cornerRadius; i++) {
            int arcDx = dxArr[i];
            // Top-left
            ctx.graphics().fill(x + cornerRadius - arcDx, y + i, x + cornerRadius, y + i + 1, color);
            // Top-right
            ctx.graphics().fill(x + width - cornerRadius, y + i, x + width - cornerRadius + arcDx, y + i + 1, color);
            // Bottom-left
            ctx.graphics().fill(x + cornerRadius - arcDx, y + height - 1 - i, x + cornerRadius, y + height - i, color);
            // Bottom-right
            ctx.graphics().fill(x + width - cornerRadius, y + height - 1 - i, x + width - cornerRadius + arcDx, y + height - i, color);
        }
    }

    // =====================================================================
    //  1-pixel rounded border
    // =====================================================================

    /** 1-pixel rounded border (outline only, no fill). */
    public void drawRoundedBorder(int x, int y,
                                  int width, int height,
                                  int cornerRadius, int color) {
        if (width <= 0 || height <= 0) return;
        cornerRadius = Math.min(cornerRadius, Math.min(width, height) / 2);
        if (cornerRadius <= 0) {
            // Simple 1-pixel rect border
            ctx.graphics().fill(x, y, x + width, y + 1, color);
            ctx.graphics().fill(x, y + height - 1, x + width, y + height, color);
            ctx.graphics().fill(x, y + 1, x + 1, y + height - 1, color);
            ctx.graphics().fill(x + width - 1, y + 1, x + width, y + height - 1, color);
            return;
        }

        // Straight edges
        ctx.graphics().fill(x + cornerRadius, y, x + width - cornerRadius, y + 1, color);
        ctx.graphics().fill(x + cornerRadius, y + height - 1, x + width - cornerRadius, y + height, color);
        ctx.graphics().fill(x, y + cornerRadius, x + 1, y + height - cornerRadius, color);
        ctx.graphics().fill(x + width - 1, y + cornerRadius, x + width, y + height - cornerRadius, color);

        // Corner arcs
        int[] dxArr = getScanlineDx(cornerRadius);
        for (int i = 0; i < cornerRadius; i++) {
            int arcDx = dxArr[i];
            int prevArcDx = (i > 0) ? dxArr[i - 1] : 0;
            ctx.graphics().fill(x + cornerRadius - arcDx, y + i, x + cornerRadius - prevArcDx, y + i + 1, color);
            ctx.graphics().fill(x + width - cornerRadius + prevArcDx, y + i, x + width - cornerRadius + arcDx, y + i + 1, color);
            ctx.graphics().fill(x + cornerRadius - arcDx, y + height - 1 - i, x + cornerRadius - prevArcDx, y + height - i, color);
            ctx.graphics().fill(x + width - cornerRadius + prevArcDx, y + height - 1 - i, x + width - cornerRadius + arcDx, y + height - i, color);
        }
    }

    // =====================================================================
    //  Triangle (vertex-based)
    // =====================================================================

    /** Cardinal direction for triangles. */
    public enum TriangleDirection { UP, DOWN, LEFT, RIGHT }

    /**
     * Solid-colour equilateral-ish triangle rendered via {@code TRIANGLES} draw mode.
     *
     * @param x         left edge of the bounding box
     * @param y         top edge of the bounding box
     * @param size      bounding box side length
     * @param color     ARGB colour
     * @param direction pointing direction
     */
    public void drawTriangle(int x, int y,
                             int size, int color, TriangleDirection direction) {
        if (size <= 0) return;

        float x1, y1, x2, y2, x3, y3;
        switch (direction) {
            case UP:
                x1 = x + size / 2f; y1 = y;
                x2 = x;             y2 = y + size;
                x3 = x + size;      y3 = y + size;
                break;
            case DOWN:
                x1 = x;             y1 = y;
                x2 = x + size;      y2 = y;
                x3 = x + size / 2f; y3 = y + size;
                break;
            case LEFT:
                x1 = x;             y1 = y + size / 2f;
                x2 = x + size;      y2 = y;
                x3 = x + size;      y3 = y + size;
                break;
            case RIGHT:
            default:
                x1 = x;             y1 = y;
                x2 = x;             y2 = y + size;
                x3 = x + size;      y3 = y + size / 2f;
                break;
        }

        drawTriangleVertices(x1, y1, x2, y2, x3, y3, color);
    }

    /**
     * Arbitrary triangle from three vertices. Useful for custom icon shapes.
        * Uses scanline rasterization via {@code graphics.fill}.
     */
    public void drawTriangleVertices(
                              float x1, float y1,
                              float x2, float y2,
                              float x3, float y3, int color) {
        // Sort vertices by Y (top to bottom)
        float tmp;
        if (y1 > y2) { tmp = x1; x1 = x2; x2 = tmp; tmp = y1; y1 = y2; y2 = tmp; }
        if (y1 > y3) { tmp = x1; x1 = x3; x3 = tmp; tmp = y1; y1 = y3; y3 = tmp; }
        if (y2 > y3) { tmp = x2; x2 = x3; x3 = tmp; tmp = y2; y2 = y3; y3 = tmp; }

        int minY = (int) Math.floor(y1);
        int maxY = (int) Math.ceil(y3);
        if (minY >= maxY) return;

        float totalH = y3 - y1;
        if (totalH < 0.001f) return;

        for (int sy = minY; sy < maxY; sy++) {
            float yf = sy + 0.5f;
            if (yf < y1 || yf > y3) continue;

            // Long edge 1→3 always spans start to end
            float xa = x1 + (x3 - x1) * (yf - y1) / totalH;
            float xb;

            if (yf < y2) {
                // Upper half: short edge 1→2
                float h12 = y2 - y1;
                xb = (h12 < 0.001f) ? x1 : x1 + (x2 - x1) * (yf - y1) / h12;
            } else {
                // Lower half: short edge 2→3
                float h23 = y3 - y2;
                xb = (h23 < 0.001f) ? x2 : x2 + (x3 - x2) * (yf - y2) / h23;
            }

            int left  = (int) Math.floor(Math.min(xa, xb));
            int right = (int) Math.ceil(Math.max(xa, xb));
            if (right > left) {
                ctx.graphics().fill(left, sy, right, sy + 1, color);
            }
        }
    }

    // =====================================================================
    //  Circle (vertex-based TRIANGLE_FAN)
    // =====================================================================

    /**
        * Filled circle using scanline fill via {@code graphics.fill}.
     *
     * @param centerX centre X
     * @param centerY centre Y
     * @param radius  radius in pixels
     * @param color   ARGB colour
     */
    public void drawCircle(int centerX, int centerY,
                           int radius, int color) {
        if (radius <= 0) return;
        for (int dy = -radius; dy <= radius; dy++) {
            int dx = (int) Math.floor(Math.sqrt((double) radius * radius - (double) dy * dy));
            ctx.graphics().fill(centerX - dx, centerY + dy, centerX + dx + 1, centerY + dy + 1, color);
        }
    }

    // =====================================================================
    //  Annulus / ring (vertex-based TRIANGLE_STRIP)
    // =====================================================================

    /**
     * Filled annulus (ring) for circular progress indicators.
        * Uses scanline fill via {@code graphics.fill}.
     *
     * @param cx         centre X
     * @param cy         centre Y
     * @param radius     outer radius
     * @param thickness  ring thickness in pixels
     * @param innerColor ARGB colour at the inner edge (used for the ring)
     * @param outerColor ARGB colour at the outer edge (used for the ring)
     */
    public void drawAnnulus(int cx, int cy,
                            int radius, int thickness,
                            int innerColor, int outerColor) {
        if (radius <= 0 || thickness <= 0) return;

        int innerRadius = Math.max(0, radius - thickness);
        // Use the average of inner and outer colour for the scanline fill
        // (vertex-colour gradient is not available in scanline mode)
        int color = lerpColor(innerColor, outerColor, 0.5f);

        for (int dy = -radius; dy <= radius; dy++) {
            int outerDx = (int) Math.floor(Math.sqrt((double) radius * radius - (double) dy * dy));
            int innerDx = (Math.abs(dy) < innerRadius)
                    ? (int) Math.ceil(Math.sqrt((double) innerRadius * innerRadius - (double) dy * dy))
                    : 0;

            // Left band
            if (cx - outerDx < cx - innerDx) {
                ctx.graphics().fill(cx - outerDx, cy + dy, cx - innerDx, cy + dy + 1, color);
            }
            // Right band
            if (cx + innerDx < cx + outerDx + 1) {
                ctx.graphics().fill(cx + innerDx, cy + dy, cx + outerDx + 1, cy + dy + 1, color);
            }
        }
    }

    /**
     * Partial annulus arc (for progress rings that fill from 0–100%).
     * Uses scanline fill with per-pixel angle testing.
     *
     * @param startAngle start angle in radians (0 = right / 3 o'clock)
     * @param endAngle   end angle in radians
     */
    public void drawAnnulusArc(int cx, int cy,
                               int radius, int thickness,
                               float startAngle, float endAngle,
                               int innerColor, int outerColor) {
        if (radius <= 0 || thickness <= 0) return;

        int innerRadius = Math.max(0, radius - thickness);
        int color = lerpColor(innerColor, outerColor, 0.5f);

        float range = endAngle - startAngle;
        if (range <= 0f) return;
        float TWO_PI = (float) (2.0 * Math.PI);
        if (range >= TWO_PI) {
            // Full ring — delegate to drawAnnulus
            drawAnnulus(cx, cy, radius, thickness, innerColor, outerColor);
            return;
        }

        for (int dy = -radius; dy <= radius; dy++) {
            int outerDx = (int) Math.floor(Math.sqrt((double) radius * radius - (double) dy * dy));
            int innerDxSq = innerRadius * innerRadius - dy * dy;

            int runStart = Integer.MIN_VALUE;
            for (int dx = -outerDx; dx <= outerDx; dx++) {
                // Check ring membership
                if (innerDxSq > 0 && dx * dx + dy * dy < innerRadius * innerRadius) {
                    // Inside the hole — flush any pending run
                    if (runStart != Integer.MIN_VALUE) {
                        ctx.graphics().fill(cx + runStart, cy + dy, cx + dx, cy + dy + 1, color);
                        runStart = Integer.MIN_VALUE;
                    }
                    continue;
                }

                // Check angle membership
                float angle = (float) Math.atan2(dy, dx);
                float a = angle - startAngle;
                a = a % TWO_PI;
                if (a < 0) a += TWO_PI;

                if (a <= range) {
                    if (runStart == Integer.MIN_VALUE) runStart = dx;
                } else {
                    if (runStart != Integer.MIN_VALUE) {
                        ctx.graphics().fill(cx + runStart, cy + dy, cx + dx, cy + dy + 1, color);
                        runStart = Integer.MIN_VALUE;
                    }
                }
            }
            // Flush remaining run
            if (runStart != Integer.MIN_VALUE) {
                ctx.graphics().fill(cx + runStart, cy + dy, cx + outerDx + 1, cy + dy + 1, color);
            }
        }
    }

    // =====================================================================
    //  Drop shadow
    // =====================================================================

    /**
     * Soft shadow behind a rounded rectangle.
     *
     * @param layers   number of shadow layers (4–8 recommended)
     * @param maxAlpha peak alpha at innermost layer (0–255)
     */
    public void drawDropShadow(int x, int y,
                               int width, int height, int cornerRadius,
                               int layers, int maxAlpha) {
        for (int i = layers; i >= 1; i--) {
            float ratio = (float) i / layers;
            int alpha = (int) (maxAlpha * ratio * ratio);
            if (alpha <= 0) continue;
            int col = alpha << 24; // black + computed alpha
            drawRoundedRect(
                    x - i, y - i,
                    width + i * 2, height + i * 2,
                    cornerRadius + i, col);
        }
    }

    /** Convenience overload — 6 layers, 40% peak alpha. */
    public void drawDropShadow(int x, int y,
                               int width, int height, int cornerRadius) {
        drawDropShadow(x, y, width, height, cornerRadius, 6, 102);
    }

    // =====================================================================
    //  Gradient helpers
    // =====================================================================

    /** Vertical gradient (top → bottom). */
    public void drawGradientV(int x, int y,
                              int width, int height,
                              int topColor, int bottomColor) {
        if (width <= 0 || height <= 0) return;
        ctx.graphics().fillGradient(x, y, x + width, y + height, topColor, bottomColor);
    }

    /** Horizontal gradient (left → right), drawn in 4-pixel bands. */
    public void drawGradientH(int x, int y,
                              int width, int height,
                              int leftColor, int rightColor) {
        if (width <= 0 || height <= 0) return;
        int step = width <= 16 ? 1 : 4;
        for (int col = 0; col < width; col += step) {
            int bandW = Math.min(step, width - col);
            float t = (float) (col + bandW / 2) / Math.max(width - 1, 1);
            int c = lerpColor(leftColor, rightColor, t);
            ctx.graphics().fill(x + col, y, x + col + bandW, y + height, c);
        }
    }

    // =====================================================================
    //  Line drawing (Bresenham)
    // =====================================================================

    /** 1-pixel line between two points. */
    public void drawLine(int x1, int y1,
                         int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            ctx.graphics().fill(x1, y1, x1 + 1, y1 + 1, color);
            if (x1 == x2 && y1 == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x1 += sx; }
            if (e2 <  dx) { err += dx; y1 += sy; }
        }
    }

    /** Thick line between two points. */
    public void drawLine(int x1, int y1,
                         int x2, int y2, int color, int thickness) {
        if (thickness <= 1) {
            drawLine(x1, y1, x2, y2, color);
            return;
        }
        int half = thickness / 2;
        boolean steep = Math.abs(y2 - y1) > Math.abs(x2 - x1);
        for (int i = -half; i <= half; i++) {
            if (steep) {
                drawLine(x1 + i, y1, x2 + i, y2, color);
            } else {
                drawLine(x1, y1 + i, x2, y2 + i, color);
            }
        }
    }

    // =====================================================================
    //  Divider helpers
    // =====================================================================

    /** Horizontal 1-pixel divider line. */
    public void drawDivider(int x, int y, int width, int color) {
        ctx.graphics().fill(x, y, x + width, y + 1, color);
    }
}

