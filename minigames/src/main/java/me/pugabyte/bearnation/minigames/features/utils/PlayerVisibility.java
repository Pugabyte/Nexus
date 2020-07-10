package me.pugabyte.bearnation.minigames.features.utils;

import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import org.bukkit.entity.Player;

public class PlayerVisibility {


	public static HidePlayer hidePlayer(Player player) {
		return new HidePlayer(player);
	}

	public static HidePlayer hidePlayer(Minigamer minigamer) {
		return new HidePlayer(minigamer.getPlayer());
	}

	public static ShowPlayer showPlayer(Player player) {
		return new ShowPlayer(player);
	}

	public static ShowPlayer showPlayer(Minigamer minigamer) {
		return new ShowPlayer(minigamer.getPlayer());
	}

	public static class HidePlayer {
		private Player player;

		public HidePlayer(Player player) {
			this.player = player;
		}

		public void from(Minigamer minigamer) {
			from(minigamer.getPlayer());
		}

		public void from(Player player) {
			player.hidePlayer(Minigames.inst(), this.player);
		}
	}

	public static class ShowPlayer {
		private Player player;

		public ShowPlayer(Player player) {
			this.player = player;
		}

		public void to(Minigamer minigamer) {
			to(minigamer.getPlayer());
		}

		public void to(Player player) {
			player.showPlayer(Minigames.inst(), this.player);
		}
	}
}
