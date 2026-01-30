package com.moddinghytale.rtscamera.camera;

public enum CameraMode {
    FIRST_PERSON(0f, true, false),
    THIRD_PERSON(8f, false, false),
    RTS(20f, false, true);

    private final float distance;
    private final boolean firstPerson;
    private final boolean showCursor;

    CameraMode(float distance, boolean firstPerson, boolean showCursor) {
        this.distance = distance;
        this.firstPerson = firstPerson;
        this.showCursor = showCursor;
    }

    public float getDistance() {
        return distance;
    }

    public boolean isFirstPerson() {
        return firstPerson;
    }

    public boolean showCursor() {
        return showCursor;
    }

    public CameraMode next() {
        CameraMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
