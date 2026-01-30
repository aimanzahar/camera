package com.moddinghytale.rtscamera.camera;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.ClientCameraView;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.packets.camera.SetServerCamera;
import com.hypixel.hytale.server.core.universe.PlayerRef;

public class CameraManager {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private final CameraPresets presets;

    public CameraManager(CameraPresets presets) {
        this.presets = presets;
    }

    public void applyMode(PlayerRef playerRef, CameraMode mode) {
        if (playerRef == null) {
            LOGGER.atWarning().log("[RTSCamera:Camera] applyMode called with null playerRef!");
            return;
        }

        LOGGER.atInfo().log("[RTSCamera:Camera] Applying mode %s to player %s", mode, playerRef.getUsername());

        if (mode == CameraMode.FIRST_PERSON) {
            resetCamera(playerRef);
            return;
        }

        ServerCameraSettings settings = presets.buildSettings(mode);
        if (settings == null) {
            LOGGER.atWarning().log("[RTSCamera:Camera] buildSettings returned null for mode %s", mode);
            return;
        }

        LOGGER.atInfo().log("[RTSCamera:Camera] Sending SetServerCamera packet: view=Custom, locked=true, distance=%.1f, cursor=%b, firstPerson=%b",
                settings.distance, settings.displayCursor, settings.isFirstPerson);

        try {
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, true, settings)
            );
            LOGGER.atInfo().log("[RTSCamera:Camera] Packet sent successfully for mode %s", mode);
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("[RTSCamera:Camera] Error sending camera packet!");
        }
    }

    public void resetCamera(PlayerRef playerRef) {
        if (playerRef == null) {
            LOGGER.atWarning().log("[RTSCamera:Camera] resetCamera called with null playerRef!");
            return;
        }

        LOGGER.atInfo().log("[RTSCamera:Camera] Resetting camera to default for player %s", playerRef.getUsername());

        try {
            playerRef.getPacketHandler().writeNoCache(
                    new SetServerCamera(ClientCameraView.Custom, false, null)
            );
            LOGGER.atInfo().log("[RTSCamera:Camera] Reset packet sent successfully.");
        } catch (Exception e) {
            LOGGER.atSevere().withCause(e).log("[RTSCamera:Camera] Error sending reset packet!");
        }
    }
}
