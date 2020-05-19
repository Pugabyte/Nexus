package me.pugabyte.bncore.features.tab;

import me.pugabyte.bncore.features.afk.AFK;
import me.pugabyte.bncore.features.scoreboard.ScoreboardLine;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Tab {

	public Tab() {
		Tasks.repeatAsync(Time.TICK, Time.SECOND.x(5), Tab::update);
	}

	public static void update() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.setPlayerListHeader(colorize(getHeader(player)));
			player.setPlayerListFooter(colorize(getFooter(player)));
			player.setPlayerListName(colorize(getFormat(player)));
		});
	}

	public static String getHeader(Player player) {
		return ScoreboardLine.ONLINE.render(player);
	}

	public static String getFooter(Player player) {
		return "  " + ScoreboardLine.PING.render(player) + "  &8&l|  " + ScoreboardLine.TPS.render(player) + "  " +
				System.lineSeparator() +
				"" +
				System.lineSeparator() +
				"&3Join us on &c/discord";
	}

	public static String getFormat(Player player) {
		Nerd nerd = new Nerd(player);
		String name = "";
		if (AFK.get(player).isAfk())
			name += "&7&o[AFK] ";
		if (nerd.isVanished())
			name += "&7&o[V] ";
		return name + nerd.getRank().getChatColor() + nerd.getName();
	}

}
