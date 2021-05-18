package me.pugabyte.nexus.features.autosort.features;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.autosort.AutoSortFeature;
import me.pugabyte.nexus.features.resourcepack.CustomModel;
import me.pugabyte.nexus.models.autosort.AutoSortUser;
import me.pugabyte.nexus.models.autosort.AutoSortUser.AutoTrashBehavior;
import me.pugabyte.nexus.models.dumpster.Dumpster;
import me.pugabyte.nexus.models.dumpster.DumpsterService;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

@NoArgsConstructor
public class AutoTrash implements Listener {

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		AutoSortUser user = AutoSortUser.of(player);
		if (!user.hasFeatureEnabled(AutoSortFeature.AUTO_TRASH))
			return;

		if (!Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK).contains(WorldGroup.get(player)))
			return;

		ItemStack item = event.getItem().getItemStack();
		ItemMeta meta = item.getItemMeta();
		if (meta.hasDisplayName() || meta.hasLore() || meta.hasEnchants() || CustomModel.exists(item))
			return;

		if (!user.getAutoTrashMaterials().contains(item.getType()))
			return;

		event.setCancelled(true);
		if (user.getAutoTrashBehavior() == AutoTrashBehavior.TRASH) {
			DumpsterService dumpsterService = new DumpsterService();
			Dumpster dumpster = dumpsterService.get();

			dumpster.add(item);
			dumpsterService.save(dumpster);

			event.getItem().remove();
		}
	}

}
