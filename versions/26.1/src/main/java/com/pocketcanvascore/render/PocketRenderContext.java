package com.pocketcanvascore.render;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Universal render adapter for Minecraft 26.1 Tiny Takeover.
 */
public interface PocketRenderContext {
    GuiGraphicsExtractor graphics();
    float delta();
}
