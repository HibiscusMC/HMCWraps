package de.skyslycer.hmcwraps.integration.zauctionhouse;

import de.skyslycer.hmcwraps.HMCWraps;
import fr.maxlego08.zauctionhouse.api.blacklist.ItemChecker;
import org.bukkit.inventory.ItemStack;

public class WrapBlacklist implements ItemChecker {

    private final HMCWraps plugin;

    public WrapBlacklist(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Wrap";
    }

    @Override
    public boolean checkItemStack(ItemStack itemStack) {
        if (!plugin.getConfiguration().getPluginIntegrations().getzAuctionHouse().isEnabled()) {
            return false;
        }
        var wrap = plugin.getWrapper().getWrap(itemStack);
        if (wrap == null) {
            return false;
        }
        var physical = plugin.getWrapper().isPhysical(itemStack);
        if (plugin.getConfiguration().getPluginIntegrations().getzAuctionHouse().isBlacklistVirtual() && !physical) {
            return true;
        }
        if (plugin.getConfiguration().getPluginIntegrations().getzAuctionHouse().isBlacklistPhysical() && physical) {
            return true;
        }
        if (plugin.getConfiguration().getPluginIntegrations().getzAuctionHouse().getBlacklistedWraps().contains(wrap.getUuid())) {
            return true;
        }
        return false;
    }

}
