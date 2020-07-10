package me.pugabyte.bearnation.server.features.achievements;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.annotations.Disabled;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.features.achievements.menu.AchievementProvider;

@Disabled
@Aliases("ach")
@Permission("achievements.use")
public class AchievementsCommand extends CustomCommand {

	public AchievementsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		AchievementProvider.open(player());
	}

}

