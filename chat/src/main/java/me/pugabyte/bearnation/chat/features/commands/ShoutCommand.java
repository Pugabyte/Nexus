package me.pugabyte.bearnation.chat.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.models.chat.ChatService;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.features.chat.ChatManager;

public class ShoutCommand extends CustomCommand {
	private Chatter chatter;

	public ShoutCommand(CommandEvent event) {
		super(event);
		chatter = new ChatService().get(player());
	}

	@Path
	void run() {
		chatter.say(ChatManager.getMainChannel(), argsString());
	}
}
