package me.pugabyte.bearnation.chat.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.Chat;

@Permission("group.admin")
public class BroadcastCommand extends CustomCommand {

	public BroadcastCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<message...>")
	void run(String message) {
		Chat.broadcast(message);
	}

}
