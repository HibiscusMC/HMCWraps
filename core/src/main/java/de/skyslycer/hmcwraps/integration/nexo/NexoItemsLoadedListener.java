package de.skyslycer.hmcwraps.integration.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.items.UpdateCallback;
import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class NexoItemsLoadedListener implements Listener {

    private final HMCWraps plugin;

    public NexoItemsLoadedListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNexoItemsLoaded(NexoItemsLoadedEvent event) {
        try {
            registerNexoCallbackReflective("hmcwraps:nexo-modifier",
                    new UpdateCallback() {
                        @Override
                        public @Nullable ItemStack preUpdate(@NonNull ItemStack itemStack) {
                            if (plugin.getWrapper().getWrap(itemStack) != null) {
                                return null;
                            } else {
                                return itemStack;
                            }
                        }
                    }
            );
        } catch (Exception exception) {
            plugin.logSevere("Failed to register Nexo update callback for HMCWraps! This will lead to issues with wrapped Nexo items. Report this immediately!", exception);
        }
    }

    // Use reflection to call the registerUpdateCallback method as the Adventure Key class is relocated in HMCWraps but not in Nexo.
    // Consider un-relocating Adventure from HMCWraps in the future to avoid such issues, however this may lead to conflicts with other plugins on Spigot.
    public static void registerNexoCallbackReflective(String keyStr, Object updateCallback) throws Exception {
        Class<?> keyClass = Class.forName(kyoriKeyClassName());
        Object keyObj = keyClass.getMethod("key", String.class).invoke(null, keyStr);
        NexoItems.class.getMethod("registerUpdateCallback", keyClass, UpdateCallback.class).invoke(null, keyObj, updateCallback);
    }

    // this is a crime.
    // Avoids compile-time constant pool relocation.
    private static String kyoriKeyClassName() {
        char[] kyoriChars = {
                'n','e','t','.',
                'k','y','o','r','i','.',
                'a','d','v','e','n','t','u','r','e','.',
                'k','e','y','.',
                'K','e','y'
        };
        return new String(kyoriChars);
    }

}
