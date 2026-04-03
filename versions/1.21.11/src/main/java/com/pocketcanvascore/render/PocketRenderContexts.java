package com.pocketcanvascore.render;

import net.minecraft.client.gui.DrawContext;

/**
 * Factory helpers for creating PocketRenderContext adapters.
 */
public final class PocketRenderContexts {
    private PocketRenderContexts() {}

    public static PocketRenderContext of(DrawContext graphics, float delta) {
        return new PocketRenderContext12111(graphics, delta);
    }
}
