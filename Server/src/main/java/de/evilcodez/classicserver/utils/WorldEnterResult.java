package de.evilcodez.classicserver.utils;

public enum WorldEnterResult {

    SUCCESSFUL(null),
    ERROR_WORLD_FULL("World is full!"),
    ERROR_NO_PERMISSIONS("You have not enough permissions to enter this world!");

    private final String message;

    WorldEnterResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
