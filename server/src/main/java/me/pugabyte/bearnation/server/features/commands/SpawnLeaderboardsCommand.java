package me.pugabyte.bearnation.server.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.server.features.listeners.Leaderboards.Leaderboard;

@Permission("group.staff")
public class SpawnLeaderboardsCommand extends CustomCommand {

	public SpawnLeaderboardsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("update <leaderboard>")
	void update(Leaderboard leaderboard) {
		leaderboard.update();
		send(PREFIX + "Updated");
	}

}
