package me.pugabyte.bearnation.chat.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class EchoCommand extends CustomCommand {

	public EchoCommand(CommandEvent event) {
		super(event);
	}

	@Path("[string...]")
	void echo(@Arg(" ") String string) {
		send(string);
	}
}
