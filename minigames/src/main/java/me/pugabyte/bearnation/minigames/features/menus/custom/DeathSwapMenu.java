package me.pugabyte.bearnation.minigames.features.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.managers.ArenaManager;
import me.pugabyte.bearnation.minigames.features.mechanics.DeathSwap;
import me.pugabyte.bearnation.minigames.features.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.arenas.DeathSwapArena;
import org.bukkit.entity.Player;

@CustomMechanicSettings(DeathSwap.class)
public class DeathSwapMenu extends MenuUtils implements InventoryProvider {
	DeathSwapArena arena;

	public DeathSwapMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, DeathSwapArena.class);
		this.arena.write();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		//contents
		contents.set(0, 0, ClickableItem.from(backItem(), e -> Minigames.getMenus().openArenaMenu(player, arena)));

		//
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
