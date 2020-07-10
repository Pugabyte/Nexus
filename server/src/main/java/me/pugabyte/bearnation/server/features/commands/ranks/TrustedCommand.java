package me.pugabyte.bearnation.server.features.commands.ranks;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class TrustedCommand extends CustomCommand {

	public TrustedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trusted() {
		line(5);
		send("&3The &eTrusted &3rank is awarded to those who are &eactive &3in the community, are &erespectful &3and &ehelpful &3to others, and follow the rules of the server.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
