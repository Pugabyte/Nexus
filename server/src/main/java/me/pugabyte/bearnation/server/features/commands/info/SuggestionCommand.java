package me.pugabyte.bearnation.server.features.commands.info;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class SuggestionCommand extends CustomCommand {

	public SuggestionCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void suggestion() {
		send(json("&3Make &esuggestions &3on our &c/discord").command("/discord"));
	}
}
