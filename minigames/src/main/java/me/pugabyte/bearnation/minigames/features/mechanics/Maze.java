package me.pugabyte.bearnation.minigames.features.mechanics;

import me.pugabyte.bearnation.minigames.features.mechanics.common.CheckpointMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Maze extends CheckpointMechanic {

	@Override
	public String getName() {
		return "Maze";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.OAK_LEAVES);
	}

}
