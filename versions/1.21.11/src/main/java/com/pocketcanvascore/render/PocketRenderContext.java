package com.pocketcanvascore.render;

import net.minecraft.client.gui.DrawContext;

/**
 * Universal render adapter for Minecraft 1.21.11.
 */
public interface PocketRenderContext {
    DrawContext graphics();
    float delta();
}
