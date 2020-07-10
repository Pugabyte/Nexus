package me.pugabyte.bearnation.creative.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class DLRequestCommand extends CustomCommand {

	public DLRequestCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (!player().getWorld().getName().equalsIgnoreCase("creative"))
			error("You must be in the creative world to run this command.");

		runCommand("ticket Plot download request");
	}
}
