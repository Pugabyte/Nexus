package me.pugabyte.bearnation.server.features.commands.info;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class IPCommand extends CustomCommand {

	public IPCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void ip() {
		send("&ebnn.gg");
	}
}
