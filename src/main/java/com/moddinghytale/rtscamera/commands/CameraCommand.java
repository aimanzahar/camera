package com.moddinghytale.rtscamera.commands;

import com.moddinghytale.rtscamera.camera.CameraManager;
import com.moddinghytale.rtscamera.camera.CameraMode;
import com.moddinghytale.rtscamera.player.PlayerStateManager;
import hytale.server.command.CommandRegistry;
import hytale.server.world.entity.player.Player;

import java.util.UUID;

public class CameraCommand {

    private final PlayerStateManager stateManager;
    private final CameraManager cameraManager;

    public CameraCommand(PlayerStateManager stateManager, CameraManager cameraManager) {
        this.stateManager = stateManager;
        this.cameraManager = cameraManager;
    }

    public void register(CommandRegistry commandRegistry) {
        commandRegistry.register("rtscam", (sender, args) -> {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players.");
                return;
            }

            UUID uuid = player.getUuid();

            if (args.length == 0) {
                CameraMode current = stateManager.getMode(uuid);
                player.sendMessage("[RTSCamera] Current mode: " + current.name());
                return;
            }

            switch (args[0].toLowerCase()) {
                case "reset", "fp" -> {
                    stateManager.setMode(uuid, CameraMode.FIRST_PERSON);
                    cameraManager.applyMode(player, CameraMode.FIRST_PERSON);
                    player.sendMessage("[RTSCamera] Switched to FIRST_PERSON.");
                }
                case "tp" -> {
                    stateManager.setMode(uuid, CameraMode.THIRD_PERSON);
                    cameraManager.applyMode(player, CameraMode.THIRD_PERSON);
                    player.sendMessage("[RTSCamera] Switched to THIRD_PERSON.");
                }
                case "rts" -> {
                    stateManager.setMode(uuid, CameraMode.RTS);
                    cameraManager.applyMode(player, CameraMode.RTS);
                    player.sendMessage("[RTSCamera] Switched to RTS.");
                }
                default -> {
                    player.sendMessage("[RTSCamera] Usage: /rtscam [reset|fp|tp|rts]");
                }
            }
        });
    }
}
