package com.moddinghytale.rtscamera;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.moddinghytale.rtscamera.camera.CameraManager;
import com.moddinghytale.rtscamera.camera.CameraPresets;
import com.moddinghytale.rtscamera.commands.CameraCommand;
import com.moddinghytale.rtscamera.config.PluginConfig;
import com.moddinghytale.rtscamera.input.ClickToMoveHandler;
import com.moddinghytale.rtscamera.input.InputHandler;
import com.moddinghytale.rtscamera.player.PlayerStateManager;

public class RTSCameraPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private PlayerStateManager stateManager;

    public RTSCameraPlugin(JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("[RTSCamera] Constructor called - plugin loaded.");
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("[RTSCamera] setup() called - beginning initialization...");

        try {
            PluginConfig config = PluginConfig.load();
            LOGGER.atInfo().log("[RTSCamera] Config loaded: thirdPersonDist=%.1f, rtsDist=%.1f, lerpSpeed=%.2f, clickToMove=%b, middleClickCycle=%b",
                    config.getThirdPersonDistance(), config.getRtsDistance(), config.getCameraLerpSpeed(),
                    config.isClickToMoveEnabled(), config.isMiddleClickCycleEnabled());

            stateManager = new PlayerStateManager();
            LOGGER.atInfo().log("[RTSCamera] PlayerStateManager created.");

            CameraPresets presets = new CameraPresets(config);
            CameraManager cameraManager = new CameraManager(presets);
            LOGGER.atInfo().log("[RTSCamera] CameraManager created.");

            ClickToMoveHandler clickToMoveHandler = new ClickToMoveHandler();

            InputHandler inputHandler = new InputHandler(stateManager, cameraManager, clickToMoveHandler, config);
            inputHandler.register(getEventRegistry());
            LOGGER.atInfo().log("[RTSCamera] InputHandler registered with EventRegistry.");

            CameraCommand cameraCommand = new CameraCommand(stateManager, cameraManager);
            getCommandRegistry().registerCommand(cameraCommand);
            LOGGER.atInfo().log("[RTSCamera] /rtscam command registered.");

            getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
                PlayerRef playerRef = event.getPlayerRef();
                LOGGER.atInfo().log("[RTSCamera] Player disconnected: %s", playerRef.getUuid());
                stateManager.removePlayer(playerRef.getUuid());
            });
            LOGGER.atInfo().log("[RTSCamera] PlayerDisconnectEvent listener registered.");

            LOGGER.atInfo().log("[RTSCamera] ===== PLUGIN FULLY ENABLED =====");
            LOGGER.atInfo().log("[RTSCamera] Middle-click to cycle: FIRST_PERSON -> THIRD_PERSON -> RTS");
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("[RTSCamera] FATAL ERROR during setup!");
        }
    }
}
