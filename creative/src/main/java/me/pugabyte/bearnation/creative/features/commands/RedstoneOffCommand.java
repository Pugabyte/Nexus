package me.pugabyte.bearnation.creative.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class RedstoneOffCommand extends CustomCommand {

	public RedstoneOffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("plot set redstone false");
	}

}
