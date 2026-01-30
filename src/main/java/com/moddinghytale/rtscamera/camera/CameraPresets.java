package com.moddinghytale.rtscamera.camera;

import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.MouseInputType;
import com.hypixel.hytale.protocol.MovementForceRotationType;
import com.hypixel.hytale.protocol.PositionDistanceOffsetType;
import com.hypixel.hytale.protocol.RotationType;
import com.hypixel.hytale.protocol.ServerCameraSettings;
import com.hypixel.hytale.protocol.Vector3f;
import com.moddinghytale.rtscamera.config.PluginConfig;

public class CameraPresets {

    private final PluginConfig config;

    public CameraPresets(PluginConfig config) {
        this.config = config;
    }

    public ServerCameraSettings buildSettings(CameraMode mode) {
        return switch (mode) {
            case FIRST_PERSON -> null; // null signals a camera reset
            case THIRD_PERSON -> buildThirdPerson();
            case RTS -> buildRTS();
        };
    }

    private ServerCameraSettings buildThirdPerson() {
        ServerCameraSettings s = new ServerCameraSettings();
        s.isFirstPerson = false;
        s.distance = config.getThirdPersonDistance();
        s.displayCursor = false;
        s.positionLerpSpeed = 0.15f;
        s.rotationLerpSpeed = 0.15f;
        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffsetRaycast;
        return s;
    }

    private ServerCameraSettings buildRTS() {
        ServerCameraSettings s = new ServerCameraSettings();
        s.positionLerpSpeed = config.getCameraLerpSpeed();
        s.rotationLerpSpeed = config.getCameraLerpSpeed();
        s.distance = config.getRtsDistance();
        s.displayCursor = true;
        s.isFirstPerson = false;
        s.movementForceRotationType = MovementForceRotationType.Custom;
        s.movementForceRotation = new Direction(-0.7853981634f, 0.0f, 0.0f);
        s.eyeOffset = true;
        s.positionDistanceOffsetType = PositionDistanceOffsetType.DistanceOffset;
        s.rotationType = RotationType.Custom;
        s.rotation = new Direction(0.0f, -1.5707964f, 0.0f);
        s.mouseInputType = MouseInputType.LookAtPlane;
        s.planeNormal = new Vector3f(0.0f, 1.0f, 0.0f);
        return s;
    }
}
