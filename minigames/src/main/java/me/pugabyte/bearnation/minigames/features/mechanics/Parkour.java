package me.pugabyte.bearnation.minigames.features.mechanics;

import me.pugabyte.bearnation.minigames.features.mechanics.common.CheckpointMechanic;
import me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers.MinigamerDeathEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Parkour extends CheckpointMechanic {

	@Override
	public String getName() {
		return "Parkour";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.FEATHER);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		event.setDeathMessage(null);
		super.onDeath(event);
	}

}
