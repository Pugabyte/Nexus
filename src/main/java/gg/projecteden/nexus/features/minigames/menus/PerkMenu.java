package gg.projecteden.nexus.features.minigames.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.menus.perks.BuyPerksMenu;
import gg.projecteden.nexus.features.minigames.menus.perks.CategoryMenu;
import gg.projecteden.nexus.features.minigames.menus.perks.PlayerPerksMenu;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PerkMenu extends MenuUtils implements InventoryProvider {
	@Override
	public void open(Player player, int page) {
		SmartInventory.builder()
				.provider(this)
				.title("Minigame Collectibles")
				.size(3, 9)
				.build()
				.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ClickableItem yourPerks = ClickableItem.from(new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player).name("&6&lYour Perks").build(), e -> new CategoryMenu<>(PlayerPerksMenu.class).open(player));
		ClickableItem buyPerks = ClickableItem.from(new ItemBuilder(Material.EMERALD).name("&a&lBuy Perks").build(), e -> new CategoryMenu<>(BuyPerksMenu.class).open(player));
		contents.set(SlotPos.of(1, 3), yourPerks);
		contents.set(SlotPos.of(1, 5), buyPerks);
	}
}
