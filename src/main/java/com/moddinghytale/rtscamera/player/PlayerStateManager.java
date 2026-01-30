package com.moddinghytale.rtscamera.player;

import com.moddinghytale.rtscamera.camera.CameraMode;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStateManager {

    private final Map<UUID, CameraMode> playerModes = new ConcurrentHashMap<>();

    public CameraMode getMode(UUID playerId) {
        return playerModes.getOrDefault(playerId, CameraMode.FIRST_PERSON);
    }

    public CameraMode cycleMode(UUID playerId) {
        CameraMode current = getMode(playerId);
        CameraMode next = current.next();
        playerModes.put(playerId, next);
        return next;
    }

    public void setMode(UUID playerId, CameraMode mode) {
        playerModes.put(playerId, mode);
    }

    public void removePlayer(UUID playerId) {
        playerModes.remove(playerId);
    }

    public void clear() {
        playerModes.clear();
    }
}
