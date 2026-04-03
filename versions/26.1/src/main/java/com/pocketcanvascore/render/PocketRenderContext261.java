package com.pocketcanvascore.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Objects;

/**
 * 26.1 adapter wrapping GuiGraphics and DeltaTracker.
 */
public final class PocketRenderContext261 implements PocketRenderContext {
    private final GuiGraphics graphics;
    private final DeltaTracker deltaTracker;

    public PocketRenderContext261(GuiGraphics graphics, DeltaTracker deltaTracker) {
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.deltaTracker = Objects.requireNonNull(deltaTracker, "deltaTracker");
    }

    @Override
    public GuiGraphics graphics() {
        return graphics;
    }

    @Override
    public float delta() {
        return deltaTracker.getGameTimeDeltaPartialTick(false);
    }
}
