package me.pugabyte.bearnation.minigames.features.menus.teams;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.ColorType;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.Team;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiFunction;

public class TeamsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;

	public TeamsMenu(@NonNull Arena arena) {
		this.arena = arena;
	}

	static void openAnvilMenu(Player player, Arena arena, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(Minigames.inst(), player, text, onComplete, p -> Minigames.tasks().wait(1, () -> Minigames.getMenus().getTeamMenus().openTeamsMenu(player, arena)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> Minigames.getMenus().openArenaMenu(player, arena));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&aAdd Team"),
			e -> openAnvilMenu(player, arena, "Default", (p, text) -> {
				arena.getTeams().add(new Team(text));
				arena.write();
				Minigames.getMenus().getTeamMenus().openTeamsMenu(player, arena);
				return AnvilGUI.Response.text(text);
			})));

		int row = 1;
		int column = 0;
		for (Team team : arena.getTeams()) {
			ItemStack item = new ItemStack(ColorType.of(team.getColor()).getWool());
			contents.set(row, column, ClickableItem.from(nameItem(item, "&e" + team.getColoredName()),
					e -> Minigames.getMenus().getTeamMenus().openTeamsEditorMenu(player, arena, team)));

			if (column != 8) {
				column++;
			} else {
				column = 1;
				row++;
			}
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}