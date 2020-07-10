package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class SitCommand extends CustomCommand {

	public SitCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("chairs sit");
	}
}
