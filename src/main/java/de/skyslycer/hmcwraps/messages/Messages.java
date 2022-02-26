package de.skyslycer.hmcwraps.messages;

import org.jetbrains.annotations.NotNull;

public enum Messages {

    NO_PERMISSION("no-permission");

    private String key;

    Messages(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

}
