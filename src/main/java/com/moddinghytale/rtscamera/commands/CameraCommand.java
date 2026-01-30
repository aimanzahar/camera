package com.moddinghytale.rtscamera.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.moddinghytale.rtscamera.camera.CameraManager;
import com.moddinghytale.rtscamera.camera.CameraMode;
import com.moddinghytale.rtscamera.player.PlayerStateManager;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class CameraCommand extends AbstractPlayerCommand {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final PlayerStateManager stateManager;
    private final CameraManager cameraManager;
    private final DefaultArg<String> modeArg;

    public CameraCommand(PlayerStateManager stateManager, CameraManager cameraManager) {
        super("rtscam", "Manage RTS camera modes. Usage: /rtscam [reset|fp|tp|rts]");
        this.setPermissionGroup(GameMode.Adventure);
        this.setAllowsExtraArguments(true);
        this.stateManager = stateManager;
        this.cameraManager = cameraManager;
        this.modeArg = withDefaultArg("mode", "Camera mode (reset/fp/tp/rts)", ArgTypes.STRING, "", "");
    }

    @Override
    protected void execute(CommandContext ctx, Store<EntityStore> store,
                           Ref<EntityStore> ref, PlayerRef playerRef, World world) {
        UUID uuid = playerRef.getUuid();
        String subCommand = resolveSubCommand(ctx);

        LOGGER.atInfo().log("[RTSCamera:Cmd] /rtscam executed by %s, subCommand='%s'", playerRef.getUsername(), subCommand);

        switch (subCommand) {
            case "reset", "fp", "first", "firstperson", "1" -> {
                stateManager.setMode(uuid, CameraMode.FIRST_PERSON);
                cameraManager.applyMode(playerRef, CameraMode.FIRST_PERSON);
                ctx.sendMessage(Message.raw("[RTSCamera] Switched to FIRST_PERSON."));
            }
            case "tp", "third", "thirdperson", "2" -> {
                stateManager.setMode(uuid, CameraMode.THIRD_PERSON);
                cameraManager.applyMode(playerRef, CameraMode.THIRD_PERSON);
                ctx.sendMessage(Message.raw("[RTSCamera] Switched to THIRD_PERSON."));
            }
            case "rts", "3" -> {
                stateManager.setMode(uuid, CameraMode.RTS);
                cameraManager.applyMode(playerRef, CameraMode.RTS);
                ctx.sendMessage(Message.raw("[RTSCamera] Switched to RTS."));
            }
            case "help", "--help", "-h" -> ctx.sendMessage(Message.raw(
                    "[RTSCamera] Usage: /rtscam [reset|fp|tp|rts]"));
            default -> {
                CameraMode current = stateManager.getMode(uuid);
                ctx.sendMessage(Message.raw("[RTSCamera] Current mode: " + current.name()
                        + ". Usage: /rtscam [reset|fp|tp|rts]"));
            }
        }
    }

    private String resolveSubCommand(CommandContext ctx) {
        String subCommand = null;
        if (ctx.provided(modeArg)) {
            subCommand = modeArg.get(ctx);
        }
        if (subCommand == null || subCommand.trim().isEmpty()) {
            subCommand = parseFromInput(ctx);
        }
        if (subCommand == null) {
            return "";
        }
        return subCommand.trim().toLowerCase(Locale.ROOT);
    }

    private String parseFromInput(CommandContext ctx) {
        String input = ctx.getInputString();
        if (input == null) {
            return "";
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1).trim();
            if (trimmed.isEmpty()) {
                return "";
            }
        }

        String[] parts = trimmed.split("\\s+");
        if (parts.length == 0) {
            return "";
        }

        int argIndex = 0;
        String commandName = ctx.getCalledCommand().getName();
        Set<String> aliases = ctx.getCalledCommand().getAliases();
        String first = parts[0].toLowerCase(Locale.ROOT);
        if (commandName != null && first.equals(commandName.toLowerCase(Locale.ROOT))) {
            argIndex = 1;
        } else if (aliases != null && aliases.contains(first)) {
            argIndex = 1;
        }

        if (argIndex >= parts.length) {
            return "";
        }

        String token = parts[argIndex];
        if (token.startsWith("--mode=")) {
            return token.substring("--mode=".length());
        }
        if (token.equalsIgnoreCase("--mode") && argIndex + 1 < parts.length) {
            return parts[argIndex + 1];
        }
        return token;
    }
}
