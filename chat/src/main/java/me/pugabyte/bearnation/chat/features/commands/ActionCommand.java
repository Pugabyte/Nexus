package me.pugabyte.bearnation.chat.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Aliases({"me", "eme", "describe", "edescribe", "eaction"})
public class ActionCommand extends CustomCommand {

	public ActionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send("&cTemporarily disabled");
	}

}
