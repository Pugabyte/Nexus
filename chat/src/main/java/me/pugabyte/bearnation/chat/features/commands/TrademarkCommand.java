package me.pugabyte.bearnation.chat.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.models.chat.ChatService;
import me.pugabyte.bearnation.chat.models.chat.Chatter;

@Aliases("tm")
public class TrademarkCommand extends CustomCommand {
	private Chatter chatter;

	public TrademarkCommand(CommandEvent event) {
		super(event);
		chatter = new ChatService().get(player());
	}

	@Path
	void run() {
		chatter.say(argsString() + "™");
	}

}
