package me.pugabyte.bearnation.minigames.features.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.mechanics.MechanicType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MechanicsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public MechanicsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> Minigames.getMenus().openArenaMenu(player, arena)));
		int row = 1;
		int column = 0;
		for (MechanicType mechanic : MechanicType.values()) {
			ItemStack menuItem = mechanic.get().getMenuItem();
			if (menuItem == null) continue;

			ItemStack item = nameItem(menuItem.clone(), "&e" + mechanic.get().getName());

			if (arena.getMechanicType() == mechanic)
				addGlowing(item);

			contents.set(row, column, ClickableItem.from(item, e -> {
				arena.setMechanicType(mechanic);
				arena.write();
				Minigames.getMenus().openMechanicsMenu(player, arena);
			}));

			if (column != 8) {
				column++;
			} else {
				column = 0;
				row++;
			}
		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
