package me.pugabyte.bearnation.chat.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.models.chat.ChatService;
import me.pugabyte.bearnation.chat.models.chat.Chatter;

@Aliases("r")
public class ReplyCommand extends CustomCommand {
	private Chatter chatter;

	public ReplyCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatService(Chat.inst()).get(player());
	}

	@Path("[message...]")
	void reply(String message) {
		if (chatter.getLastPrivateMessage() == null)
			error("No one has messaged you");

		if (isNullOrEmpty(message))
			chatter.setActiveChannel(chatter.getLastPrivateMessage());
		else
			chatter.say(chatter.getLastPrivateMessage(), message);
	}
}
