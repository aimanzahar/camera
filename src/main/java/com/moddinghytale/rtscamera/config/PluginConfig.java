package com.moddinghytale.rtscamera.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class PluginConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private float thirdPersonDistance = 8.0f;
    private float rtsDistance = 20.0f;
    private float cameraLerpSpeed = 0.2f;
    private boolean clickToMoveEnabled = true;
    private boolean middleClickCycleEnabled = true;

    public static PluginConfig load() {
        try (InputStream is = PluginConfig.class.getClassLoader()
                .getResourceAsStream("rtscamera-config.json")) {
            if (is == null) {
                return new PluginConfig();
            }
            try (InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                PluginConfig config = GSON.fromJson(reader, PluginConfig.class);
                return config != null ? config : new PluginConfig();
            }
        } catch (IOException e) {
            return new PluginConfig();
        }
    }

    public float getThirdPersonDistance() {
        return thirdPersonDistance;
    }

    public float getRtsDistance() {
        return rtsDistance;
    }

    public float getCameraLerpSpeed() {
        return cameraLerpSpeed;
    }

    public boolean isClickToMoveEnabled() {
        return clickToMoveEnabled;
    }

    public boolean isMiddleClickCycleEnabled() {
        return middleClickCycleEnabled;
    }
}
