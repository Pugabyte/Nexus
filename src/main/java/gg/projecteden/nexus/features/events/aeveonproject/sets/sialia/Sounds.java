package gg.projecteden.nexus.features.events.aeveonproject.sets.sialia;

import gg.projecteden.nexus.features.events.aeveonproject.sets.APSetType;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.util.Collection;

import static gg.projecteden.nexus.features.events.aeveonproject.APUtils.APLoc;
import static gg.projecteden.nexus.features.events.aeveonproject.AeveonProject.getWGUtils;

public class Sounds {
	private static final Location engineLoc = APLoc(-1294, 86, -1056);
	private static final Sound shipSound = Sound.BLOCK_BEACON_AMBIENT;
	private static final Sound engineSound = Sound.ENTITY_MINECART_RIDING;

	public Sounds() {
		// Engine Sound
		Tasks.repeatAsync(0, Time.TICK.x(30), () -> {
			if (!APSetType.SIALIA.get().isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = getWGUtils().getPlayersInRegion(APSetType.SIALIA.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, engineSound, SoundCategory.AMBIENT, 2.5F, 1F);
				}
			});
		});

		// Ship Sound
		Tasks.repeatAsync(0, Time.SECOND.x(5), () -> {
			if (!APSetType.SIALIA.get().isActive())
				return;

			Tasks.sync(() -> {
				Collection<Player> players = getWGUtils().getPlayersInRegion(APSetType.SIALIA.get().getRegion());
				for (Player player : players) {
					if (player.getInventory().getHelmet() != null && player.getInventory().getHelmet().getType().equals(Material.LEATHER_HELMET))
						continue;

					player.playSound(engineLoc, shipSound, SoundCategory.AMBIENT, 50F, 1F);
					Tasks.wait(Time.SECOND.x(2), () -> player.playSound(engineLoc, shipSound, SoundCategory.AMBIENT, 50F, 1F));
				}
			});
		});
	}


}
