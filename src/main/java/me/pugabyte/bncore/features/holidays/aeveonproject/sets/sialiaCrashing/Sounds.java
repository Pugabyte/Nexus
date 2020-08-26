package me.pugabyte.bncore.features.holidays.aeveonproject.sets.sialiaCrashing;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.Regions;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Collection;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WGUtils;
import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WORLD;

public class Sounds implements Listener {
	private static final Location engineLoc = new Location(WORLD, -823, 86, -1062);
	private static final Sound engineSound = Sound.ENTITY_MINECART_RIDING;
	private static final Sound warningSound = Sound.ENTITY_ELDER_GUARDIAN_CURSE;

	public Sounds() {
		BNCore.registerListener(this);

		// Engine Sound
		Tasks.repeatAsync(0, Time.TICK.x(30), () -> {
			if (!SialiaCrashing.isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = WGUtils.getPlayersInRegion(Regions.sialiaCrashing);
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, engineSound, SoundCategory.AMBIENT, 2.5F, 1F);
				}
			});
		});

		// Alarm Sound
		Tasks.repeatAsync(0, Time.TICK.x(50), () -> {
			if (!SialiaCrashing.isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = WGUtils.getPlayersInRegion(Regions.sialiaCrashing);
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, warningSound, SoundCategory.AMBIENT, 0.5F, 0.8F);
				}
			});
		});
	}

}