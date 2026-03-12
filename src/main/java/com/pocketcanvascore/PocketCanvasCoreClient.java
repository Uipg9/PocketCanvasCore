package com.pocketcanvascore;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PocketCanvasCoreClient implements ClientModInitializer {
    public static final String MOD_ID = "pocketcanvascore";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("[PocketCanvasCore] Rendering API v{} initialized.", "1.0.0");
    }
}
