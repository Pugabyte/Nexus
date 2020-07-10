package me.pugabyte.bearnation.survival.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class HopperCommand extends CustomCommand {

	public HopperCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void run(@Arg("on") boolean enable) {
		runCommand("chopper " + (enable ? "on" : "off"));
	}
}
