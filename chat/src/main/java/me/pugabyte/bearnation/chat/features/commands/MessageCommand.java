package me.pugabyte.bearnation.chat.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.models.chat.ChatService;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.chat.models.chat.PrivateChannel;
import me.pugabyte.bearnation.features.chat.Chat;

@Aliases({"m", "msg", "w", "whisper", "t", "tell", "pm", "dm"})
public class MessageCommand extends CustomCommand {
	private Chatter chatter;

	public MessageCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = Chat.PREFIX;
		chatter = new ChatService().get(player());
	}

	@Path("<player> [message...]")
	void message(Chatter to, String message) {
		if (chatter == to)
			error("You cannot message yourself");

		PrivateChannel dm = new PrivateChannel(chatter, to);
		if (isNullOrEmpty(message))
			chatter.setActiveChannel(dm);
		else
			chatter.say(dm, message);
	}
}
