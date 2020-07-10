package me.pugabyte.bearnation.minigames.features.menus.custom;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.managers.ArenaManager;
import me.pugabyte.bearnation.minigames.features.mechanics.GoldRush;
import me.pugabyte.bearnation.minigames.features.menus.annotations.CustomMechanicSettings;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.arenas.GoldRushArena;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static me.pugabyte.bearnation.minigames.Minigames.PREFIX;

@CustomMechanicSettings(GoldRush.class)
public class GoldRushMenu extends MenuUtils implements InventoryProvider {
	GoldRushArena arena;

	public GoldRushMenu(Arena arena){
		this.arena = ArenaManager.convert(arena, GoldRushArena.class);
		this.arena.write();
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		openAnvilMenu(Minigames.inst(), player, text, onComplete, p -> Minigames.tasks().wait(1, () -> Minigames.getMenus().openCustomSettingsMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(0, 0, ClickableItem.from(backItem(), e-> Minigames.getMenus().openArenaMenu(player, arena)));

		String currentValue = (arena.getMineStackHeight() > 0) ? "" + arena.getMineStackHeight() : "null";

		contents.set(1, 4, ClickableItem.from(nameItem(Material.LADDER, "&eMine Stack Height", "&eCurrent value:||&3"),
				e -> {
					openAnvilMenu(player, arena, currentValue, (Player p, String text) -> {
						if(!Utils.isInt(text)) {
							AnvilGUI.Response.close();
							throw new InvalidInputException(PREFIX + "You must use an integer for Mine Stack Height.");
						}
						arena.setMineStackHeight(Integer.parseInt(text));
						ArenaManager.write(arena);
						Minigames.getMenus().openCustomSettingsMenu(player, arena);
						return AnvilGUI.Response.text(text);
				});
		}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}
}
