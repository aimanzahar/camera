package com.moddinghytale.rtscamera.input;

import com.moddinghytale.rtscamera.camera.CameraManager;
import com.moddinghytale.rtscamera.camera.CameraMode;
import com.moddinghytale.rtscamera.config.PluginConfig;
import com.moddinghytale.rtscamera.player.PlayerStateManager;
import hytale.server.event.EventRegistry;
import hytale.server.world.entity.player.Player;
import hytale.server.world.entity.player.event.PlayerMouseButtonEvent;
import hytale.shared.math.Vector3i;
import hytale.shared.world.entity.player.input.MouseButtonEvent;
import hytale.shared.world.entity.player.input.MouseButtonState;
import hytale.shared.world.entity.player.input.MouseButtonType;

import java.util.UUID;

public class InputHandler {

    private final PlayerStateManager stateManager;
    private final CameraManager cameraManager;
    private final ClickToMoveHandler clickToMoveHandler;
    private final PluginConfig config;

    public InputHandler(PlayerStateManager stateManager, CameraManager cameraManager,
                        ClickToMoveHandler clickToMoveHandler, PluginConfig config) {
        this.stateManager = stateManager;
        this.cameraManager = cameraManager;
        this.clickToMoveHandler = clickToMoveHandler;
        this.config = config;
    }

    public void register(EventRegistry eventRegistry) {
        eventRegistry.register(PlayerMouseButtonEvent.class, event -> {
            MouseButtonEvent mouse = event.getMouseButton();
            Player player = event.getPlayer();
            UUID uuid = player.getUuid();
            CameraMode mode = stateManager.getMode(uuid);

            // Middle-click = cycle camera mode
            if (config.isMiddleClickCycleEnabled()
                    && mouse.mouseButtonType == MouseButtonType.Middle
                    && mouse.state == MouseButtonState.Pressed) {
                CameraMode newMode = stateManager.cycleMode(uuid);
                cameraManager.applyMode(player, newMode);
                event.setCancelled(true);
                return;
            }

            // Left-click in RTS mode = click-to-move
            if (config.isClickToMoveEnabled()
                    && mode == CameraMode.RTS
                    && mouse.mouseButtonType == MouseButtonType.Left
                    && mouse.state == MouseButtonState.Pressed) {
                Vector3i targetBlock = event.getTargetBlock();
                if (targetBlock != null) {
                    clickToMoveHandler.movePlayerTo(player, targetBlock);
                    event.setCancelled(true);
                }
            }
        });
    }
}
