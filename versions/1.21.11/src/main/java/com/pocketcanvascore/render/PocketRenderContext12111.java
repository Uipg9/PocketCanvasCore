package com.pocketcanvascore.render;

import net.minecraft.client.gui.DrawContext;

import java.util.Objects;

/**
 * 1.21.11 adapter wrapping DrawContext and raw frame delta.
 */
public final class PocketRenderContext12111 implements PocketRenderContext {
    private final DrawContext graphics;
    private final float delta;

    public PocketRenderContext12111(DrawContext graphics, float delta) {
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.delta = delta;
    }

    @Override
    public DrawContext graphics() {
        return graphics;
    }

    @Override
    public float delta() {
        return delta;
    }
}
