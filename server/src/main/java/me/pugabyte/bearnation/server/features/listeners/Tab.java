package me.pugabyte.bearnation.server.features.listeners;

import me.pugabyte.bearnation.BNCore;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.features.afk.AFK;
import me.pugabyte.bearnation.features.scoreboard.ScoreboardLine;
import me.pugabyte.bearnation.server.models.afk.events.AFKEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.pugabyte.bearnation.api.utils.StringUtils.colorize;

public class Tab implements Listener {

	public Tab() {
		BNCore.registerListener(this);
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
		String name = nerd.getRank().getChatColor() + nerd.getName();
		if ("KodaBear".equals(nerd.getName())) name = "&5KodaBear";
		if (AFK.get(player).isAfk())
			name += " &7&o[AFK]";
		if (nerd.isVanished())
			name += " &7&o[V]";
		return name.trim();
	}

	@EventHandler
	public void onAFKChange(AFKEvent event) {
		update();
	}

}
