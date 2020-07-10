package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;

public class NameCommand extends CustomCommand {

	public NameCommand(CommandEvent event) {
		super(event);
	}

	@Path("<uuid>")
	void uuid(Nerd nerd) {
		send(json("&e" + nerd.getName())
				.hover("&3Shift+Click to insert into your chat")
				.insert(nerd.getName()));
	}

}
