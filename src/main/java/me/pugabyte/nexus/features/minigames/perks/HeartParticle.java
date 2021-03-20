package me.pugabyte.nexus.features.minigames.perks;

import me.pugabyte.nexus.features.minigames.models.perks.PerkCategory;
import me.pugabyte.nexus.features.minigames.models.perks.common.TickablePerk;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeartParticle extends TickablePerk {
	@Override
	public String getName() {
		return "Heart";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.RED_TULIP);
	}

	@Override
	public String[] getDescription() {
		return new String[]{"TODO"};
	}

	@Override
	public PerkCategory getCategory() {
		return PerkCategory.PARTICLE;
	}

	@Override
	public int getPrice() {
		return 1;
	}

	@Override
	public void tick(Player player) {
		particle(player, Particle.HEART, 0.7d);
	}
}