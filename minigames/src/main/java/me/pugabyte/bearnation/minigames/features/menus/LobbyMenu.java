package me.pugabyte.bearnation.minigames.features.menus;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.BiFunction;

import static me.pugabyte.bearnation.minigames.Minigames.PREFIX;

public class LobbyMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public LobbyMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(Minigames.inst(), player, text, onComplete, p -> Minigames.tasks().wait(1, () -> Minigames.getMenus().openLobbyMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> Minigames.getMenus().openArenaMenu(player, arena));

		contents.set(1, 2, ClickableItem.from(nameItem(
				Material.OAK_DOOR,
				"&eLobby Location",
				"&3Current Lobby Location:" + "||" + getLocationLore(arena.getLobby().getLocation()) + "|| ||&eClick to set to current location"
			),
			e -> {
				arena.getLobby().setLocation(player.getLocation());
				arena.write();
				Minigames.getMenus().openLobbyMenu(player, arena);
			}));

		contents.set(1, 6, ClickableItem.from(nameItem(
				Material.CLOCK,
				"&eWait Time",
				"&3Current Wait Time:||&e" + arena.getLobby().getWaitTime()
			),
			e -> openAnvilMenu(player, arena, String.valueOf(arena.getLobby().getWaitTime()), (Player p, String text) -> {
				if (Utils.isInt(text)) {
					arena.getLobby().setWaitTime(Integer.parseInt(text));
					arena.write();
					Minigames.getMenus().openLobbyMenu(player, arena);
					return AnvilGUI.Response.text(text);
				} else {
					player.sendMessage(StringUtils.colorize(PREFIX + "You must use an integer for wait time."));
					return AnvilGUI.Response.close();
				}
			})));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
