package me.pugabyte.bearnation.server.features.commands.ranks;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class VeteranCommand extends CustomCommand {

	public VeteranCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void veteran() {
		line(5);
		send("&3The &6&lVeteran &3rank is given to &eex-staff &3members, to show our appreciation for their help making Bear Nation what it is today.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
