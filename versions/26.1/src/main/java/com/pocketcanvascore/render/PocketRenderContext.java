package com.pocketcanvascore.render;

import net.minecraft.client.gui.GuiGraphics;

/**
 * Universal render adapter for Minecraft 26.1 Tiny Takeover.
 */
public interface PocketRenderContext {
    GuiGraphics graphics();
    float delta();
}
