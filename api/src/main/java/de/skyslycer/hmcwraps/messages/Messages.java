package de.skyslycer.hmcwraps.messages;

import org.jetbrains.annotations.NotNull;

public enum Messages {

    NO_PERMISSION("no-permission"),
    NO_PERMISSION_FOR_WRAP("no-permission-for-wrap"),
    APPLY_WRAP("apply-wrap"),
    REMOVE_WRAP("remove-wrap"),
    NO_ITEM("no-item"),
    NO_WRAPS("no-wraps"),
    PREVIEW_DISABLED("preview.disabled"),
    PREVIEW_BAR("preview.bar"),
    COMMAND_PLAYER_ONLY("command.player-only"),
    COMMAND_INVALID_WRAP("command.invalid-wrap"),
    COMMAND_INVALID_PHYSICAL("command.invalid-physical"),
    COMMAND_GIVEN_PHYSICAL("command.given-physical"),
    COMMAND_GIVEN_UNWRAPPER("command.given-unwrapper"),
    COMMAND_RELOAD("command.reload"),
    COMMAND_NEED_ITEM("command.need-item"),
    COMMAND_ITEM_NOT_FOR_WRAP("command.item-not-for-wrap"),
    COMMAND_WRAP_APPLIED("command.wrap-applied"),
    COMMAND_NO_MATCHING_ITEM("command.no-matching-item"),
    COMMAND_PREVIEW_CREATED("command.preview-created"),
    PLACEHOLDER_AVAILABLE("placeholder.available"),
    PLACEHOLDER_NOT_AVAILABLE("placeholder.not-available");

    private final String key;

    Messages(String key) {
        this.key = key;
    }

    /**
     * Get the properties key based on an enum value.
     * @return The key
     */
    @NotNull
    public String getKey() {
        return key;
    }

}
