package de.skyslycer.hmcwraps.debug;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.debug.*;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.ColorUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import gs.mclo.java.MclogsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DebugCreator {

    private static final URI UPLOAD_URL = URI.create("https://pasteapi.skyslycer.de/post");
    private static final String DEBUG_URL = "https://paste.skyslycer.de/%s";

    public static DebugConfig createDebugConfig(HMCWrapsPlugin plugin) {
        return new DebugConfig(plugin.getConfiguration());
    }

    public static DebugInformation createDebugInformation(HMCWrapsPlugin plugin) {
        var latest = plugin.getUpdateChecker().getLatest();
        var protocolLib = getVersionOfPlugin("ProtocolLib");
        var serverSoftware = Bukkit.getName();
        var serverVersion = Bukkit.getBukkitVersion();
        var iaVersion = getVersionOfPlugin("ItemsAdder");
        var oraxenVersion = getVersionOfPlugin("Oraxen");
        var mythicMobsVersion = getVersionOfPlugin("MythicMobs");
        var crucibleVersion = getVersionOfPlugin("Crucible");
        return new DebugInformation(plugin.getDescription().getVersion(), latest == null ? "Current" : latest.version(), protocolLib, serverVersion, serverSoftware, iaVersion, oraxenVersion, mythicMobsVersion, crucibleVersion);
    }

    private static String getVersionOfPlugin(String plugin) {
        String version = "Not Installed";
        if (Bukkit.getPluginManager().getPlugin(plugin) != null) {
            version = Bukkit.getPluginManager().getPlugin(plugin).getDescription().getVersion();
        }
        return version;
    }

    public static DebugWraps createDebugWraps(HMCWrapsPlugin plugin) {
        return new DebugWraps(plugin.getWrapsLoader().getCollections(), plugin.getWrapsLoader().getWraps().entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> plugin.getCollectionHelper().getCollection(entry.getValue()))));
    }

    public static DebugWrap createDebugWrap(HMCWrapsPlugin plugin, Wrap wrap) {
        var collection = plugin.getCollectionHelper().getCollection(wrap);
        return new DebugWrap(wrap, collection, plugin.getCollectionHelper().getMaterials(collection));
    }

    public static DebugPlayer createDebugPlayer(HMCWrapsPlugin plugin, Player player) {
        return new DebugPlayer(plugin.getFavoriteWrapStorage().get(player).stream().map(Wrap::getUuid).toList(),
                plugin.getFilterStorage().get(player), createDebugItemData(plugin, player.getInventory().getItemInMainHand()));
    }

    public static DebugItemData createDebugItemData(HMCWrapsPlugin plugin, ItemStack item) {
        if (item.getType().isAir()) {
            return null;
        }
        var nbt = NBT.itemStackToNBT(item);
        var wrapper = plugin.getWrapper();
        var wrap = wrapper.getWrap(item);
        var color = "N/A";
        if (item.getItemMeta() instanceof LeatherArmorMeta) {
            color = ColorUtil.colorToHex(((LeatherArmorMeta) item.getItemMeta()).getColor());
        }
        return new DebugItemData(
                item.getType().toString(),
                wrapper.isPhysical(item),
                wrap == null ? "-" : wrap.getUuid(),
                wrapper.getOwningPlayer(item),
                wrapper.isPhysicalUnwrapper(item),
                wrapper.getPhysicalWrapper(item),
                wrapper.getOriginalData(item),
                wrapper.getFakeDurability(item),
                wrapper.getFakeMaxDurability(item),
                item.getItemMeta().getCustomModelData(),
                color,
                nbt.toString()
        );
    }

    public static String debugToJson(Debuggable debuggable) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(debuggable);
    }

    public static Optional<String> upload(String text, String type) {
        var client = HttpClient.newHttpClient();
        try {
            var request = client.send(
                    HttpRequest.newBuilder().headers(
                            "User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:101.0) Gecko/20100101 Firefox/101.0",
                            "Content-Type",
                            "text/" + type
                    ).uri(UPLOAD_URL).POST(HttpRequest.BodyPublishers.ofString(text)).build(),
                    HttpResponse.BodyHandlers.ofString()
            );
            var key = request.body();
            if (key.contains("Request Entity Too Large")) {
                return Optional.of("Too large");
            }
            key = new Gson().fromJson(key, JsonObject.class).get("key").getAsString();
            return Optional.of(String.format(DEBUG_URL, key));
        } catch (Exception exception) {
            Bukkit.getLogger().severe("Failed to upload HMCWraps debug information! Please check the error below and report this!");
            exception.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<String> uploadLog(Path path) {
        try {
            var response = MclogsAPI.share(path);
            if (response != null && response.success) {
                return Optional.of(response.url);
            }
        } catch (Exception exception) {
            Bukkit.getLogger().severe("Failed to upload server log through HMCWraps! Please check the error below and report this!");
            exception.printStackTrace();
        }
        return Optional.empty();
    }

}
