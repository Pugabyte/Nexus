package me.pugabyte.bearnation.survival.features.shops;

import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.models.shop.Shop;
import me.pugabyte.bearnation.models.shop.ShopService;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class ShopUtils {

	public static void giveItem(OfflinePlayer player, ItemStack item) {
		if (player.isOnline())
			Utils.giveItem(player.getPlayer(), item);
		else
			((Shop) new ShopService().get(player)).getHolding().add(item);
	}

}
