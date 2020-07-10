package me.pugabyte.bearnation.creative.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Permission("group.moderator")
public class EntityCapCommand extends CustomCommand {

	public EntityCapCommand(CommandEvent event) {
		super(event);
	}

	@Path("<amount>")
	void run(@Arg("50") int amount) {
		runCommand("plot set entity-cap " + amount);
		send("&3Set the entity cap to " + amount);
	}

}
