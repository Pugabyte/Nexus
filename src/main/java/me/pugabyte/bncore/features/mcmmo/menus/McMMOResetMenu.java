package me.pugabyte.bncore.features.mcmmo.menus;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.entity.Player;

public class McMMOResetMenu {

	public static void openMcMMOReset(Player player) {
		SmartInventory inv = SmartInventory.builder()
				.provider(new McMMOResetProvider())
				.size(6, 9)
				.title("&3McMMO Reset")
				.build();
		inv.open(player);
	}
}
