package de.skyslycer.hmcwraps.messages;

import org.jetbrains.annotations.NotNull;

public enum Messages {

    NO_PERMISSION("no-permission"),
    NO_PERMISSION_FOR_WRAP("no-permission-for-wrap"),
    APPLY_WRAP("apply-wrap"),
    REMOVE_WRAP("remove-wrap"),
    PREVIEW_DISABLED("preview.disabled"),
    PREVIEW_BAR("preview.bar"),
    PREVIEW_NOT_ENOUGH_SPACE("preview.not-enough-space"),
    COMMAND_PLAYER_ONLY("command.player-only"),
    COMMAND_INVALID_WRAP("command.invalid-wrap"),
    COMMAND_INVALID_PHYSICAL("command.invalid-physical"),
    COMMAND_INVALID_REMOVER("command.invalid-remover"),
    COMMAND_GIVEN_PHYSICAL("command.given-physical"),
    COMMAND_GIVEN_UNWRAPPER("command.given-unwrapper"),
    COMMAND_RELOAD("command.reload"),
    PLACEHOLDER_AVAILABLE("placeholder.available"),
    PLACEHOLDER_NOT_AVAILABLE("placeholder.not-available");

    private final String key;

    Messages(String key) {
        this.key = key;
    }

    @NotNull
    public String getKey() {
        return key;
    }

}
