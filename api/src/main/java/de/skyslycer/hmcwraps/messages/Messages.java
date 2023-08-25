package de.skyslycer.hmcwraps.messages;

import org.jetbrains.annotations.NotNull;

public enum Messages {

    NO_PERMISSION("no-permission"),
    NO_PERMISSION_FOR_WRAP("no-permission-for-wrap"),
    APPLY_WRAP("apply-wrap"),
    REMOVE_WRAP("remove-wrap"),
    NO_ITEM("no-item"),
    NO_WRAPS("no-wraps"),
    NO_REWRAP("no-rewrap"),
    INVENTORY_FILTER_ACTIVE("inventory.filter.active"),
    INVENTORY_FILTER_INACTIVE("inventory.filter.inactive"),
    PREVIEW_DISABLED("preview.disabled"),
    PREVIEW_BAR("preview.bar"),
    COMMAND_MISSING_ARGUMENT("command.missing-argument"),
    COMMAND_PLAYER_ONLY("command.player-only"),
    COMMAND_INVALID_WRAP("command.invalid-wrap"),
    COMMAND_INVALID_PHYSICAL("command.invalid-physical"),
    COMMAND_GIVEN_PHYSICAL("command.given-physical"),
    COMMAND_GIVEN_UNWRAPPER("command.given-unwrapper"),
    COMMAND_RELOAD("command.reload"),
    COMMAND_NEED_ITEM("command.need-item"),
    COMMAND_ITEM_NOT_FOR_WRAP("command.item-not-for-wrap"),
    COMMAND_WRAP_WRAPPED("command.wrap.wrapped"),
    COMMAND_NO_MATCHING_ITEM("command.no-matching-item"),
    COMMAND_PREVIEW_CREATED("command.preview-created"),
    COMMAND_HELP_HEADER("command.help.header"),
    COMMAND_HELP_FORMAT("command.help.format"),
    COMMAND_HELP_NO_PERMISSION("command.help.no-permission"),
    COMMAND_LIST_HEADER("command.list.header"),
    COMMAND_LIST_COLLECTIONS("command.list.collections"),
    COMMAND_LIST_WRAPS("command.list.wraps"),
    COMMAND_LIST_COLLECTIONS_FORMAT("command.list.collections-format"),
    COMMAND_LIST_WRAPS_FORMAT("command.list.wraps-format"),
    COMMAND_LIST_KEY_FORMAT("command.list.key-format"),
    COMMAND_ITEM_NOT_WRAPPED("command.item-not-wrapped"),
    COMMAND_UNWRAP_UNWRAPPED("command.unwrap.unwrapped"),
    COMMAND_CONVERT_SUCCESS("command.convert.success"),
    COMMAND_CONVERT_CONFIRM("command.convert.confirm"),
    COMMAND_CONVERT_NO_CONFIRM("command.convert.no-confirm"),
    COMMAND_CONVERT_FAILED("command.convert.failed"),
    COMMAND_CREATE_FAILED("command.create.failed"),
    COMMAND_CREATE_SUCCESS("command.create.success");

    private final String key;

    Messages(String key) {
        this.key = key;
    }

    /**
     * Get the properties key based on an enum value.
     *
     * @return The key
     */
    @NotNull
    public String getKey() {
        return key;
    }

}
