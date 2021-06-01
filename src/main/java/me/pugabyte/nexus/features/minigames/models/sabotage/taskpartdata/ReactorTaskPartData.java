package me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata;

import me.pugabyte.nexus.features.menus.sabotage.tasks.ReactorTask;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.matchdata.SabotageMatchData;
import me.pugabyte.nexus.features.minigames.models.sabotage.TaskPart;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class ReactorTaskPartData extends SabotageTaskPartData {
	public ReactorTaskPartData(TaskPart task) {
		super(task);
	}

	@Override
	public int getDuration() {
		return 60;
	}

	@Override
	public boolean hasTask() {
		return true;
	}

	@Override
	public void task(Match match) {
		List<Player> players = match.getAlivePlayers().stream().filter(ReactorTask::isOpen).collect(Collectors.toList());
		if (players.size() == 2) {
			players.forEach(Player::closeInventory);
			match.<SabotageMatchData>getMatchData().endSabotage();
		}
	}
}
