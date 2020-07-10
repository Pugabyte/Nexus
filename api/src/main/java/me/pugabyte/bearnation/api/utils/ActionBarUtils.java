package me.pugabyte.bearnation.api.utils;

import me.pugabyte.bearnation.api.BNCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.pugabyte.bearnation.api.utils.StringUtils.colorize;

public class ActionBarUtils {

	public static void sendActionBar(final Player player, final String message) {
		player.sendActionBar(colorize(message));
	}

	public static void sendActionBar(final Player player, final String message, int duration) {
		sendActionBar(player, message, duration, true);
	}

	public static void sendActionBar(final Player player, final String message, int duration, boolean fade) {
		sendActionBar(player, message);

		if (!fade && duration >= 0)
			BNCore.tasks().wait(duration + 1, () -> sendActionBar(player, ""));

		while (duration > 40)
			BNCore.tasks().wait(duration -= 40, () -> sendActionBar(player, message));
	}

	public static void sendActionBarToAllPlayers(String message) {
		sendActionBarToAllPlayers(message, -1);
	}

	public static void sendActionBarToAllPlayers(String message, int duration) {
		sendActionBarToAllPlayers(message, duration, true);
	}

	public static void sendActionBarToAllPlayers(String message, int duration, boolean fade) {
		for (Player player : Bukkit.getOnlinePlayers())
			sendActionBar(player, message, duration, fade);
	}

}
