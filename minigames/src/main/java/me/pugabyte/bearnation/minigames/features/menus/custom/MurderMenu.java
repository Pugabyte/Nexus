package me.pugabyte.bearnation.minigames.features.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.managers.ArenaManager;
import me.pugabyte.bearnation.minigames.features.mechanics.Murder;
import me.pugabyte.bearnation.minigames.features.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.arenas.MurderArena;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@CustomMechanicSettings(Murder.class)
public class MurderMenu extends MenuUtils implements InventoryProvider {

	MurderArena arena;

	public MurderMenu(Arena arena) {
		this.arena = ArenaManager.convert(arena, MurderArena.class);
	}

	public SmartInventory openScrapPointsMenu(Arena arena) {
		SmartInventory INV = SmartInventory.builder()
				.id("ScrapPointsLocationsMenu")
				.size(6, 9)
				.provider(new MurderSubMenu(arena))
				.title("Scrap Points Locations Menu")
				.build();
		return INV;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(Minigames.inst(), player, text, onComplete, p -> Minigames.tasks().wait(1, () -> Minigames.getMenus().openCustomSettingsMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> Minigames.menus.openArenaMenu(player, arena));

		contents.set(1, 3, ClickableItem.from(nameItem(new ItemStack(Material.IRON_INGOT), "&eScrap Points"),
				e -> openScrapPointsMenu(arena).open(player)));

		contents.set(1, 5, ClickableItem.from(nameItem(Material.CLOCK, "&eSpawn Chance", "&3Current value:||&e" + arena.getSpawnChance()),
				e -> openAnvilMenu(player, arena, arena.getSpawnChance() + "", (Player p, String text) -> {
					if (!Utils.isInt(text)) {
						AnvilGUI.Response.close();
						throw new InvalidInputException(Minigames.PREFIX + "You must use an integer for spawn chance.");
					}
					arena.setSpawnChance(Integer.parseInt(text));
					ArenaManager.write(arena);
					Minigames.getMenus().openCustomSettingsMenu(player, arena);
					return AnvilGUI.Response.text(text);
				})));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

	public static class MurderSubMenu extends MenuUtils implements InventoryProvider {

		MurderArena arena;

		public MurderSubMenu(@NonNull Arena arena) {
			this.arena = ArenaManager.convert(arena, MurderArena.class);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			MurderMenu MurderMenu = new MurderMenu(arena);

			addBackItem(contents, e -> Minigames.menus.openArenaMenu(player, arena));

			Pagination page = contents.pagination();

			contents.set(0, 4, ClickableItem.from(nameItem(
					Material.EMERALD_BLOCK,
					"&eAdd Scrap Point Location",
					"&3Click to add a Scrap Point||&3at your current location."
				),
				e -> {
					arena.getScrapPoints().add(player.getLocation().getBlock().getLocation());
					arena.write();
					MurderMenu.openScrapPointsMenu(arena).open(player, page.getPage());
				}));

			ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a Scrap Point location with||&7me to delete the location.");
			contents.set(0, 8, ClickableItem.from(deleteItem, e -> Minigames.tasks().wait(2, () -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
				} else if (Utils.isNullOrAir(player.getItemOnCursor())) {
					player.setItemOnCursor(deleteItem);
				}
			})));

			if (arena.getScrapPoints() == null) return;

			ClickableItem[] clickableItems = new ClickableItem[arena.getScrapPoints().size()];
			List<Location> scrapPointsLocations = new ArrayList<>(arena.getScrapPoints());
			for (int i = 0; i < scrapPointsLocations.size(); i++) {
				Location scrapPointsLocation = scrapPointsLocations.get(i);
				ItemStack item = nameItem(Material.COMPASS, "&eScrap Point #" + (i + 1),
						getLocationLore(scrapPointsLocations.get(i)) + "|| ||&7Click to Teleport");

				clickableItems[i] = ClickableItem.from(item, e -> {
					if (player.getItemOnCursor().getType().equals(Material.TNT)) {
						Minigames.tasks().wait(2, () -> {
							arena.getScrapPoints().remove(scrapPointsLocation);
							arena.write();
							player.setItemOnCursor(new ItemStack(Material.AIR));
							MurderMenu.openScrapPointsMenu(arena).open(player, page.getPage());
						});
					} else {
						player.teleport(scrapPointsLocation);
					}
				});
			}

			page.setItems(clickableItems);
			page.setItemsPerPage(36);
			page.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));

			if (!page.isLast())
				contents.set(5, 8, ClickableItem.from(nameItem(new ItemStack(Material.ARROW), "&fNext Page"), e -> MurderMenu.openScrapPointsMenu(arena).open(player, page.next().getPage())));
			if (!page.isFirst())
				contents.set(5, 0, ClickableItem.from(nameItem(new ItemStack(Material.BARRIER), "&fPrevious Page"), e -> MurderMenu.openScrapPointsMenu(arena).open(player, page.previous().getPage())));
		}

		@Override
		public void update(Player player, InventoryContents inventoryContents) {

		}

	}
}
