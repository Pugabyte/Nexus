package me.pugabyte.bearnation.chat.features.commands;


import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.models.chat.ChatService;
import me.pugabyte.bearnation.chat.models.chat.PublicChannel;
import me.pugabyte.bearnation.features.chat.Censor;
import me.pugabyte.bearnation.features.chat.events.ChatEvent;
import me.pugabyte.bearnation.features.chat.events.PublicChatEvent;

import java.util.HashSet;

@Permission("group.seniorstaff")
public class CensorCommand extends CustomCommand {

	public CensorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("process <channel> <message...>")
	void process(PublicChannel channel, String message) {
		ChatEvent event = new PublicChatEvent(new ChatService().get(player()), channel, message, new HashSet<>());
		Censor.process(event);
		send(PREFIX + "Processed message:" + (event.isCancelled() ? " &c(Cancelled)" : ""));
		send("&eOriginal: &f" + message);
		send("&eResult: &f" + event.getMessage());
	}

	@Path("reload")
	void reload() {
		Censor.reloadConfig();
		send(PREFIX + Censor.getCensorItems().size() + " censor items loaded from disk");
	}

	@Path("debug")
	void debug() {
		send(Censor.getCensorItems().toString());
	}

}
