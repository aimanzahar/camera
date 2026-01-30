package com.moddinghytale.rtscamera.input;

import com.hypixel.hytale.event.EventRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.MouseButtonEvent;
import com.hypixel.hytale.protocol.MouseButtonState;
import com.hypixel.hytale.protocol.MouseButtonType;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerMouseButtonEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.moddinghytale.rtscamera.camera.CameraManager;
import com.moddinghytale.rtscamera.camera.CameraMode;
import com.moddinghytale.rtscamera.config.PluginConfig;
import com.moddinghytale.rtscamera.player.PlayerStateManager;

import java.util.UUID;

public class InputHandler {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

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
        LOGGER.atInfo().log("[RTSCamera:Input] Registering PlayerMouseButtonEvent listener...");

        eventRegistry.register(PlayerMouseButtonEvent.class, event -> {
            try {
                MouseButtonEvent mouse = event.getMouseButton();
                Player player = event.getPlayer();
                PlayerRef playerRef = event.getPlayerRefComponent();
                UUID uuid = playerRef.getUuid();
                CameraMode mode = stateManager.getMode(uuid);

                LOGGER.atInfo().log("[RTSCamera:Input] Mouse event: button=%s, state=%s, player=%s, currentMode=%s",
                        mouse.mouseButtonType, mouse.state, playerRef.getUsername(), mode);

                // Middle-click = cycle camera mode
                if (config.isMiddleClickCycleEnabled()
                        && mouse.mouseButtonType == MouseButtonType.Middle
                        && mouse.state == MouseButtonState.Pressed) {
                    CameraMode newMode = stateManager.cycleMode(uuid);
                    LOGGER.atInfo().log("[RTSCamera:Input] Middle-click detected! Cycling %s -> %s for player %s",
                            mode, newMode, playerRef.getUsername());
                    cameraManager.applyMode(playerRef, newMode);
                    event.setCancelled(true);
                    return;
                }

                // Left-click in RTS mode = click-to-move
                if (config.isClickToMoveEnabled()
                        && mode == CameraMode.RTS
                        && mouse.mouseButtonType == MouseButtonType.Left
                        && mouse.state == MouseButtonState.Pressed) {
                    Vector3i targetBlock = event.getTargetBlock();
                    LOGGER.atInfo().log("[RTSCamera:Input] Left-click in RTS mode. targetBlock=%s",
                            targetBlock != null ? "(" + targetBlock.x + "," + targetBlock.y + "," + targetBlock.z + ")" : "null");
                    if (targetBlock != null) {
                        clickToMoveHandler.movePlayerTo(player, targetBlock);
                        event.setCancelled(true);
                    }
                }
            } catch (Exception e) {
                LOGGER.atSevere().withCause(e).log("[RTSCamera:Input] Error handling mouse event!");
            }
        });

        LOGGER.atInfo().log("[RTSCamera:Input] PlayerMouseButtonEvent listener registered successfully.");
    }
}
