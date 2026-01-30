package com.moddinghytale.rtscamera;

import com.moddinghytale.rtscamera.camera.CameraManager;
import com.moddinghytale.rtscamera.camera.CameraPresets;
import com.moddinghytale.rtscamera.commands.CameraCommand;
import com.moddinghytale.rtscamera.config.PluginConfig;
import com.moddinghytale.rtscamera.input.ClickToMoveHandler;
import com.moddinghytale.rtscamera.input.InputHandler;
import com.moddinghytale.rtscamera.player.PlayerStateManager;
import hytale.server.plugin.JavaPlugin;
import hytale.server.world.entity.player.Player;
import hytale.server.world.entity.player.event.PlayerDisconnectEvent;

import java.util.logging.Logger;

public class RTSCameraPlugin extends JavaPlugin {

    private static final Logger LOGGER = Logger.getLogger("RTSCamera");

    private PlayerStateManager stateManager;
    private CameraManager cameraManager;

    @Override
    public void setup() {
        PluginConfig config = PluginConfig.load();
        LOGGER.info("RTSCamera config loaded.");

        stateManager = new PlayerStateManager();
        CameraPresets presets = new CameraPresets(config);
        cameraManager = new CameraManager(presets);
        ClickToMoveHandler clickToMoveHandler = new ClickToMoveHandler();

        InputHandler inputHandler = new InputHandler(stateManager, cameraManager, clickToMoveHandler, config);
        inputHandler.register(getEventRegistry());

        CameraCommand cameraCommand = new CameraCommand(stateManager, cameraManager);
        cameraCommand.register(getCommandRegistry());

        getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            stateManager.removePlayer(player.getUuid());
        });

        LOGGER.info("RTSCamera plugin enabled. Middle-click to cycle: FP -> TP -> RTS");
    }

    @Override
    public void onDisable() {
        if (stateManager != null) {
            stateManager.clear();
        }
        LOGGER.info("RTSCamera plugin disabled.");
    }
}
