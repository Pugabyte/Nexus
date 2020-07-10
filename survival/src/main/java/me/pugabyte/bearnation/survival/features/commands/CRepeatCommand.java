package me.pugabyte.bearnation.survival.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class CRepeatCommand extends CustomCommand {

	public CRepeatCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("cpersist");
	}
}
