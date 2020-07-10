package me.pugabyte.bearnation.minigames.features.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.managers.ArenaManager;
import me.pugabyte.bearnation.minigames.features.mechanics.Battleship;
import me.pugabyte.bearnation.minigames.features.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.arenas.BattleshipArena;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CustomMechanicSettings(Battleship.class)
public class BattleshipMenu extends MenuUtils implements InventoryProvider {

	BattleshipArena arena;

	public BattleshipMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, BattleshipArena.class);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> Minigames.menus.openArenaMenu(player, arena)));

		contents.set(1, 0, ClickableItem.empty(nameItem(Material.BLACK_CONCRETE, "")));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {}

}
