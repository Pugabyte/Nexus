package me.pugabyte.bearnation.minigames.features.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.Loadout;
import me.pugabyte.bearnation.minigames.features.models.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DeleteLoadoutMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;

	public DeleteLoadoutMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		ItemStack cancel = nameItem(Material.LIME_STAINED_GLASS_PANE, "&7Cancel");
		contents.fillRect(0, 0, 2, 8, ClickableItem.from(cancel, e -> Minigames.getMenus().getTeamMenus().openTeamsMenu(player, arena)));
		contents.fillRect(1, 1, 1, 7, ClickableItem.from(cancel, e -> Minigames.getMenus().getTeamMenus().openTeamsMenu(player, arena)));

		contents.set(1, 4, ClickableItem.from(nameItem(Material.TNT, "&4&lDELETE LOADOUT", "&7This cannot be undone."), e -> {
			team.setLoadout(new Loadout());
			arena.write();
			Minigames.getMenus().getTeamMenus().openLoadoutMenu(player, arena, team);
		}));
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
