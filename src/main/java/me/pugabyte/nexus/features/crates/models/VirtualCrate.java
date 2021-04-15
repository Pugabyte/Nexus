package me.pugabyte.nexus.features.crates.models;

import me.pugabyte.nexus.features.crates.models.exceptions.CrateOpeningException;
import org.bukkit.entity.Player;

public abstract class VirtualCrate extends Crate {

	public abstract int getCrateAmount(Player player);

	public abstract void takeCrate(Player player);

	@Override
	public void takeKey() {
		if (getCrateAmount(player) <= 0)
			throw new CrateOpeningException("You do not have any more keys");
		takeCrate(player);
	}
}
