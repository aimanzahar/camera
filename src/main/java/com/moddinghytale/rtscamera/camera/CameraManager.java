package com.moddinghytale.rtscamera.camera;

import hytale.server.world.entity.player.Player;
import hytale.server.world.entity.player.PlayerRef;
import hytale.shared.net.protocol.client.play.SetServerCamera;
import hytale.shared.world.entity.player.camera.ClientCameraView;

public class CameraManager {

    private final CameraPresets presets;

    public CameraManager(CameraPresets presets) {
        this.presets = presets;
    }

    public void applyMode(Player player, CameraMode mode) {
        PlayerRef playerRef = player.getReference();
        if (playerRef == null) return;

        if (mode == CameraMode.FIRST_PERSON) {
            resetCamera(player);
            return;
        }

        var settings = presets.buildSettings(mode);
        if (settings == null) return;

        playerRef.getPacketHandler().writeNoCache(
                new SetServerCamera(ClientCameraView.Custom, true, settings)
        );
    }

    public void resetCamera(Player player) {
        PlayerRef playerRef = player.getReference();
        if (playerRef == null) return;

        playerRef.getPacketHandler().writeNoCache(
                new SetServerCamera(ClientCameraView.Custom, false, null)
        );
    }
}
