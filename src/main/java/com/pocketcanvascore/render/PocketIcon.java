package com.pocketcanvascore.render;

/**
 * Procedural icon builder — combine primitive shapes from {@link PocketCanvas}
 * into recognisable mini-icons. Zero texture files required.
 */
public final class PocketIcon {
    private PocketIcon() {}

    // =====================================================================
    //  Stylised villager icon
    // =====================================================================

    /**
     * Draw a stylised villager head + body using rectangles and circles.
     *
     * @param context PocketRenderContext
     * @param x       left edge of the icon bounding box
     * @param y       top edge of the icon bounding box
     * @param scale   scale multiplier (1.0 = 16×24 px)
     */
    public static void drawVillagerIcon(PocketRenderContext context, int x, int y, float scale) {
        PocketCanvas canvas = new PocketCanvas(context);
        int s = (int) scale;
        if (s < 1) s = 1;

        int headW = 10 * s;
        int headH = 10 * s;
        int bodyW = 8 * s;
        int bodyH = 12 * s;

        int totalW = headW;
        int headX = x;
        int headY = y;
        int bodyX = x + (headW - bodyW) / 2;
        int bodyY = y + headH + s;

        // ── Body (robe) ─────────────────────────────────────
        canvas.drawRoundedRect(bodyX, bodyY, bodyW, bodyH, 2 * s, PocketTheme.VILLAGER_ROBE);

        // ── Head ────────────────────────────────────────────
        canvas.drawRoundedRect(headX, headY, headW, headH, 3 * s, PocketTheme.VILLAGER_SKIN);

        // ── Nose ────────────────────────────────────────────
        int noseW = 2 * s;
        int noseH = 3 * s;
        int noseX = headX + (headW - noseW) / 2;
        int noseY = headY + 4 * s;
        canvas.drawRect(noseX, noseY, noseW, noseH, PocketTheme.VILLAGER_NOSE);

        // ── Eyes ────────────────────────────────────────────
        int eyeSize = Math.max(1, s);
        int eyeY = headY + 3 * s;
        int leftEyeX  = headX + 2 * s;
        int rightEyeX = headX + headW - 2 * s - eyeSize;
        canvas.drawRect(leftEyeX, eyeY, eyeSize, eyeSize, PocketTheme.VILLAGER_EYES);
        canvas.drawRect(rightEyeX, eyeY, eyeSize, eyeSize, PocketTheme.VILLAGER_EYES);

        // ── Eyebrows (unibrow) ──────────────────────────────
        int browY = headY + 2 * s;
        int browW = headW - 4 * s;
        int browX = headX + 2 * s;
        canvas.drawRect(browX, browY, browW, Math.max(1, s / 2), PocketCanvas.darken(PocketTheme.VILLAGER_SKIN, 0.5f));
    }

    // =====================================================================
    //  Crop / farmland icon
    // =====================================================================

    /**
     * Draw a stylised crop patch: a dirt block with a growing green stalk.
     * The stalk height scales dynamically with {@code growthPercentage}.
     *
     * @param context          PocketRenderContext
     * @param x                left edge
     * @param y                top edge
     * @param scale            scale multiplier (1.0 = 16×16 px)
     * @param growthPercentage 0.0 (seed) → 1.0 (fully grown)
     */
    public static void drawCropIcon(PocketRenderContext context, int x, int y,
                                    float scale, float growthPercentage) {
        PocketCanvas canvas = new PocketCanvas(context);
        int s = (int) scale;
        if (s < 1) s = 1;
        growthPercentage = Math.clamp(growthPercentage, 0f, 1f);

        int blockW = 14 * s;
        int blockH = 6 * s;
        int blockX = x + s;
        int blockY = y + 10 * s;

        // ── Dirt block ──────────────────────────────────────
        canvas.drawRoundedRect(blockX, blockY, blockW, blockH, s, PocketTheme.CROP_DIRT);

        // Top row of dirt — slightly lighter
        canvas.drawRect(blockX + s, blockY, blockW - 2 * s, Math.max(1, s), PocketCanvas.lighten(PocketTheme.CROP_DIRT, 0.15f));

        // ── Stalks ──────────────────────────────────────────
        int maxStalkH = 9 * s;
        int stalkH = (int) (maxStalkH * growthPercentage);
        if (stalkH < 1 && growthPercentage > 0.01f) stalkH = 1;

        int stalkW = Math.max(1, s);

        if (stalkH > 0) {
            int stalkBaseY = blockY - stalkH;

            // Centre stalk
            int cx = blockX + blockW / 2 - stalkW / 2;
            canvas.drawRect(cx, stalkBaseY, stalkW, stalkH, PocketTheme.CROP_STALK);

            // Stalk tip (lighter green)
            int tipH = Math.max(1, stalkH / 4);
            canvas.drawRect(cx, stalkBaseY, stalkW, tipH, PocketTheme.CROP_TIP);

            // Side stalks once growth > 40%
            if (growthPercentage > 0.4f) {
                int sideH = (int) (stalkH * 0.5f);
                int sideY = blockY - sideH;

                // Left stalk
                int lx = cx - 3 * s;
                if (lx >= blockX) {
                    canvas.drawRect(lx, sideY, stalkW, sideH, PocketTheme.CROP_STALK);
                }
                // Right stalk
                int rx = cx + 3 * s;
                if (rx + stalkW <= blockX + blockW) {
                    canvas.drawRect(rx, sideY, stalkW, sideH, PocketTheme.CROP_STALK);
                }
            }

            // Wheat head once growth > 80%
            if (growthPercentage > 0.8f) {
                int headW = 3 * s;
                int headH = 2 * s;
                canvas.drawRect(cx - s, stalkBaseY - headH, headW, headH, PocketTheme.CROP_WHEAT);
            }
        }
    }
}
