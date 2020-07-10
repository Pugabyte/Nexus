package me.pugabyte.bearnation.minigames.features.mechanics;

import me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bearnation.minigames.features.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class FreeForAll extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Free For All";
	}

	@Override
	public String getDescription() {
		return "Kill everyone!";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.IRON_SWORD);
	}

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getAttacker() != null)
			event.getAttacker().scored();
		super.onDeath(event);
	}

}
