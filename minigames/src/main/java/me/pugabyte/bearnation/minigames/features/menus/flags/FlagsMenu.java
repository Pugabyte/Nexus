package me.pugabyte.bearnation.minigames.features.menus.flags;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.ColorType;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FlagsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public FlagsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e -> Minigames.getMenus().openArenaMenu(player, arena)));

		ColorType color = arena.isWhitelist() ? ColorType.WHITE : ColorType.BLACK;
		contents.set(1, 0, ClickableItem.from(nameItem(color.getWool(), "&eUsable Block List",
				"&7Click me to set the block list||&7that players can use|| ||&3Current Setting: &e" + color), e -> {
			Minigames.getMenus().blockListMenu(arena).open(player);
		}));

		ItemStack lateJoinItem = nameItem(Material.IRON_DOOR, "&eLate Join",
				"&7Set if players can join after||&7the game has started|| ||&3Allowed:||&e" + arena.canJoinLate());
		if (arena.canJoinLate())
			addGlowing(lateJoinItem);

		contents.set(1, 1, ClickableItem.from(lateJoinItem, e -> {
			arena.canJoinLate(!arena.canJoinLate());
			arena.write();
			Minigames.getMenus().openFlagsMenu(player, arena);
		}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
