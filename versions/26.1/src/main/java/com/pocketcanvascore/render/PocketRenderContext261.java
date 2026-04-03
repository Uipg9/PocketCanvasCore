package com.pocketcanvascore.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.Objects;

/**
 * 26.1 adapter wrapping GuiGraphicsExtractor and DeltaTracker.
 */
public final class PocketRenderContext261 implements PocketRenderContext {
    private final GuiGraphicsExtractor graphics;
    private final DeltaTracker deltaTracker;

    public PocketRenderContext261(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        this.graphics = Objects.requireNonNull(graphics, "graphics");
        this.deltaTracker = Objects.requireNonNull(deltaTracker, "deltaTracker");
    }

    @Override
    public GuiGraphicsExtractor graphics() {
        return graphics;
    }

    @Override
    public float delta() {
        return deltaTracker.getGameTimeDeltaPartialTick(false);
    }
}
