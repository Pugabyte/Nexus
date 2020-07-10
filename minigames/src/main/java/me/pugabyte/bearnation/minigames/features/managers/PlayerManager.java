package me.pugabyte.bearnation.minigames.features.managers;

import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import org.bukkit.entity.Player;

public class PlayerManager {

	public static Minigamer get(Player player) {
		for (Match match : MatchManager.getAll()) {
			for (Minigamer minigamer : match.getMinigamers()) {
				if (minigamer.getPlayer().equals(player)) {
					return minigamer;
				}
			}
		}

		return new Minigamer(player);
	}

}
