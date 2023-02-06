package de.skyslycer.hmcwraps.placeholderapi;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.serialization.wrap.WrappableItem;
import de.skyslycer.hmcwraps.util.ColorUtil;
import de.skyslycer.hmcwraps.util.StringUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import java.util.Map;

public class HMCWrapsPlaceholders extends PlaceholderExpansion {

    private final HMCWraps plugin;

    public HMCWrapsPlaceholders(HMCWraps plugin) {
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
        } else if (identifier.split("_").length == 2) {
            var split = identifier.split("_");
            var wrap = plugin.getWraps().get(split[0]);
            if (wrap == null) {
                return null;
            }
            switch (split[1]) {
                case "modelid" -> {
                    return String.valueOf(wrap.getModelId());
                }
                case "color" -> {
                    if (wrap.getColor() == null) {
                        return null;
                    }
                    return ColorUtil.colorToHex(wrap.getColor());
                }
                case "type" -> {
                    for (Map.Entry<String, WrappableItem> wrappableItem : plugin.getWrappableItems().entrySet()) {
                        if (!wrappableItem.getValue().getWraps().values().stream().map(Wrap::getUuid).toList().isEmpty()) {
                            return wrappableItem.getKey();
                        }
                    }
                    return null;
                }
            }
        }
        return null;
    }

}
