package com.pocketcanvascore.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Factory helpers for creating PocketRenderContext adapters.
 */
public final class PocketRenderContexts {
    private PocketRenderContexts() {}

    public static PocketRenderContext of(GuiGraphics graphics, DeltaTracker deltaTracker) {
        return new PocketRenderContext261(graphics, deltaTracker);
    }
}
