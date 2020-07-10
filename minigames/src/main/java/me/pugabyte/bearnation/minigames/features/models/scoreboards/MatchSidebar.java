package me.pugabyte.bearnation.minigames.features.models.scoreboards;

import me.pugabyte.bearnation.api.utils.BNScoreboard;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MatchSidebar implements MinigameScoreboard {
	private Match match;
	private BNScoreboard scoreboard;

	public MatchSidebar(Match match) {
		this.match = match;
		this.scoreboard = new BNScoreboard(match.getArena().getMechanic().getScoreboardTitle(match));;
	}

	@Override
	public void update() {
		scoreboard.setTitle(match.getArena().getMechanic().getScoreboardTitle(match));
		scoreboard.setLines(match.getArena().getMechanic().getScoreboardLines(match));

		for (Player player : Bukkit.getOnlinePlayers())
			if (!match.getPlayers().contains(player))
				scoreboard.unsubscribe(player);

		scoreboard.subscribe(match.getPlayers());
	}

	@Override
	public void handleJoin(Minigamer minigamer) {
		scoreboard.subscribe(minigamer.getPlayer());
		update();
	}

	@Override
	public void handleQuit(Minigamer minigamer) {
		scoreboard.unsubscribe(minigamer.getPlayer());
		update();
	}

	@Override
	public void handleEnd() {
		scoreboard.delete();
	}

}
