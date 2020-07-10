package me.pugabyte.bearnation.minigames.features.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeleteArenaMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public DeleteArenaMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemStack cancelItem = nameItem(new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1), "&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.from(cancelItem, e -> Minigames.getMenus().openArenaMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.from(cancelItem, e -> Minigames.getMenus().openArenaMenu(player, arena)));

		contents.set(1, 4, ClickableItem.from(nameItem(Material.TNT, "&4&lDELETE ARENA", "&7This cannot be undone."),
				e -> {
					arena.delete();
					player.sendMessage(StringUtils.colorize(Minigames.PREFIX + "Arena &e" + arena.getName() + " &3deleted"));
				}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
