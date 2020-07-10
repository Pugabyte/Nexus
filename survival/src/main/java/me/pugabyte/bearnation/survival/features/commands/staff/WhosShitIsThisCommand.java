package me.pugabyte.bearnation.survival.features.commands.staff;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class WhosShitIsThisCommand extends CustomCommand {

	public WhosShitIsThisCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommand("co lookup action:drop radius:10 time:1000d");
	}

}
