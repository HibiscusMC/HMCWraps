package de.skyslycer.hmcwraps.messages;

import org.jetbrains.annotations.NotNull;

public enum Messages {

    NO_PERMISSION("no-permission"),
    NO_PERMISSION_FOR_WRAP("no-permission-for-wrap"),
    APPLY_WRAP("apply-wrap"),
    REMOVE_WRAP("remove-wrap"),
    NO_ITEM("no-item"),
    NO_ITEM_SELECTED("no-item-selected"),
    NO_WRAPS("no-wraps"),
    NO_REWRAP("no-rewrap"),
    NO_FILE("no-file"),
    BAD_FOLDER("bad-folder"),
    INVALID_FORMAT("invalid-format"),
    ARMOR_IMITATION_FORBIDDEN_INVENTORY("armor-imitation.forbidden-inventory"),
    INVENTORY_FILTER_ACTIVE("inventory.filter.active"),
    INVENTORY_FILTER_INACTIVE("inventory.filter.inactive"),
    PREVIEW_DISABLED("preview.disabled"),
    PREVIEW_BAR("preview.bar"),
    FAVORITES_SET("favorites.set"),
    FAVORITES_UNSET("favorites.unset"),
    FAVORITES_CLEAR("favorites.clear"),
    COMMAND_MISSING_ARGUMENT("command.missing-argument"),
    COMMAND_PLAYER_ONLY("command.player-only"),
    COMMAND_INVALID_WRAP("command.invalid-wrap"),
    COMMAND_INVALID_PHYSICAL("command.invalid-physical"),
    COMMAND_GIVEN_PHYSICAL("command.given-physical"),
    COMMAND_GIVEN_UNWRAPPER("command.given-unwrapper"),
    COMMAND_INVALID_WORLD("command.invalid-world"),
    COMMAND_INVALID_PLAYER("command.invalid-player"),
    COMMAND_DROPPED_PHYSICAL("command.dropped-physical"),
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
    COMMAND_ITEM_NOT_WRAPPED_SELF("command.item-not-wrapped-self"),
    COMMAND_UNWRAP_UNWRAPPED("command.unwrap.unwrapped"),
    COMMAND_CONVERT_SUCCESS("command.convert.success"),
    COMMAND_CONVERT_CONFIRM("command.convert.confirm"),
    COMMAND_CONVERT_NO_CONFIRM("command.convert.no-confirm"),
    COMMAND_CONVERT_FAILED("command.convert.failed"),
    COMMAND_CREATE_FAILED("command.create.failed"),
    COMMAND_CREATE_SUCCESS("command.create.success"),
    COMMAND_OPEN("command.open"),
    COMMAND_REPAIR("command.repair"),
    COMMAND_PERMISSION_EXPORT_RUNNING("command.permission.export.running"),
    COMMAND_PERMISSION_EXPORT_FINISHED("command.permission.export.finished"),
    COMMAND_PERMISSION_EXPORT_FAILED("command.permission.export.failed"),
    COMMAND_PERMISSION_IMPORT_RUNNING("command.permission.import.running"),
    COMMAND_PERMISSION_IMPORT_ALREADY_IMPORTED("command.permission.import.already-imported"),
    COMMAND_PERMISSION_IMPORT_FINISHED("command.permission.import.finished"),
    COMMAND_PERMISSION_IMPORT_FAILED("command.permission.import.failed"),
    COMMAND_PERMISSION_REVERT_RUNNING("command.permission.revert.running"),
    COMMAND_PERMISSION_REVERT_ALREADY_REVERTED("command.permission.revert.already-reverted"),
    COMMAND_PERMISSION_REVERT_FINISHED("command.permission.revert.finished"),
    COMMAND_PERMISSION_REVERT_FAILED("command.permission.revert.failed"),
    PLACEHOLDER_EQUIPPED("placeholder.equipped"),
    PLACEHOLDER_NOT_EQUIPPED("placeholder.not-equipped"),
    PLACEHOLDER_FAVORITE("placeholder.favorite"),
    PLACEHOLDER_NOT_FAVORITE("placeholder.not-favorite"),
    PLACEHOLDER_HAS_PERMISSION("placeholder.has-permission"),
    PLACEHOLDER_NO_PERMISSION("placeholder.no-permission"),
    PLACEHOLDER_INVALID_WRAP("placeholder.invalid-wrap");

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
