package me.pugabyte.bearnation.survival.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.MaterialTag;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.api.utils.WorldGroup;
import me.pugabyte.bearnation.models.tip.Tip;
import me.pugabyte.bearnation.models.tip.Tip.TipType;
import me.pugabyte.bearnation.models.tip.TipService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class ConcreteCommand extends CustomCommand implements Listener {

	public ConcreteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void concrete() {
		Inventory inv = Bukkit.createInventory(null, 27, StringUtils.colorize("&6Concrete Exchange"));
		player().openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (!event.getView().getTitle().equals(StringUtils.colorize("&6Concrete Exchange"))) return;
		for (ItemStack item : event.getInventory().getContents()) {
			if (Utils.isNullOrAir(item)) continue;
			if (!MaterialTag.CONCRETE_POWDERS.isTagged(item.getType())) {
				event.getPlayer().getInventory().addItem(item);
				continue;
			}
			item.setType(Material.valueOf(item.getType().name().replace("_POWDER", "")));
			event.getPlayer().getInventory().addItem(item);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (WorldGroup.get(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!MaterialTag.ALL_CONCRETES.isTagged(event.getBlock().getType()))
			return;

		TipService tipService = new TipService();
		Tip tip = tipService.get(event.getPlayer());
		if (tip.show(TipType.CONCRETE))
			send(event.getPlayer(), "&3Did you know? &e- &3You can use &c/concrete &3to easily convert concrete powder into hardened concrete.");
	}


}
