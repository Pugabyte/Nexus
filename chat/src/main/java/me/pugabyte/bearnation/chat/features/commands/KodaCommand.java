package me.pugabyte.bearnation.chat.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.features.chat.Koda;

@Permission("group.seniorstaff")
public class KodaCommand extends CustomCommand {

	public KodaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<message...>")
	void say(String message) {
		Koda.say(message);
	}

	@Path("reload")
	void reload() {
		Koda.reloadConfig();
		send(PREFIX + Koda.getTriggers().size() + " responses loaded from disk");
	}

}
