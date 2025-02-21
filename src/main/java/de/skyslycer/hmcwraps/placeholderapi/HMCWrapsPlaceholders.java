package de.skyslycer.hmcwraps.placeholderapi;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.util.ColorUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.Map;

public class HMCWrapsPlaceholders extends PlaceholderExpansion {

    private final HMCWrapsPlugin plugin;

    public HMCWrapsPlaceholders(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "hmcwraps";
    }

    @Override
    public String getAuthor() {
        return "Skyslycer";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(org.bukkit.entity.Player player, String identifier) {
        if (identifier.equals("mainhand") && player != null) {
            var wrap = plugin.getWrapper().getWrap(player.getInventory().getItemInMainHand());
            if (wrap == null) {
                return null;
            }
            return wrap.getUuid();
        } else if (identifier.equals("filter") && player != null) {
            if (plugin.getFilterStorage().get(player)) {
                return StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, plugin.getMessageHandler().get(Messages.INVENTORY_FILTER_ACTIVE)));
            } else {
                return StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, plugin.getMessageHandler().get(Messages.INVENTORY_FILTER_INACTIVE)));
            }
        } else if (identifier.split("_").length >= 2) {
            var action = identifier.substring(0, identifier.indexOf("_"));
            var wrapUuid = identifier.substring(identifier.indexOf("_") + 1);
            var wrap = plugin.getWrapsLoader().getWraps().get(wrapUuid);
            switch (action) {
                case "equipped" -> { // Check if the specified wrap is the one equipped on the item the player is wrapping in the virtual inventory
                    if (player == null) {
                        return null;
                    }
                    var equipped = plugin.getWrapGui().get(player.getUniqueId());
                    return wrapUuid.equals(equipped) ?
                            StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, plugin.getMessageHandler().get(Messages.PLACEHOLDER_EQUIPPED)))
                            : StringUtil.LEGACY_SERIALIZER.serialize(StringUtil.parseComponent(player, plugin.getMessageHandler().get(Messages.PLACEHOLDER_NOT_EQUIPPED)));
                }
                case "modelid" -> {
                    if (wrap == null) {
                        return null;
                    }
                    return String.valueOf(wrap.getModelId());
                }
                case "color" -> {
                    if (wrap == null || wrap.getColor() == null) {
                        return null;
                    }
                    return ColorUtil.colorToHex(wrap.getColor());
                }
                case "type" -> {
                    return plugin.getWrapsLoader().getTypeWraps().entrySet().stream().filter(it -> it.getValue().contains(wrapUuid))
                            .findFirst().map(Map.Entry::getKey).orElse(null);
                }
            }
        }
        return null;
    }

}
