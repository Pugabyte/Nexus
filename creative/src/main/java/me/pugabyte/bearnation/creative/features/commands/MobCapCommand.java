package me.pugabyte.bearnation.creative.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class MobCapCommand extends CustomCommand {

	public MobCapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<amount>")
	void run(@Arg("50") int amount) {
		runCommand("plot set mob-cap " + amount);
		runCommand("plot set hostile-cap " + amount);
		runCommand("plot set animal-cap " + amount);
		send("&3Set the mob cap to " + amount);
	}

}
