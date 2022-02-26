package de.skyslycer.hmcwraps.messages;

import org.jetbrains.annotations.NotNull;

public enum Messages {

    NO_PERMISSION("no-permission"),
    NO_WRAPS("no-wraps"),
    PLACEHOLDER_AVAILABLE("placeholder.available"),
    PLACEHOLDER_NOT_AVAILABLE("placeholder.not-available");

    private String key;

    Messages(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

}
