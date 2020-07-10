package me.pugabyte.bearnation.server.features.commands.staff;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.models.nerd.Rank;

@Permission("group.seniorstaff")
public class DemoteCommand extends CustomCommand {

	public DemoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void promote(Nerd nerd) {
		Rank rank = nerd.getRank();
		Rank previous = rank.previous();
		if (rank == previous)
			error("User is already min rank");

		runCommandAsConsole("lp user " + nerd.getName() + " parent set " + previous.name());
		send(PREFIX + "Demoted " + nerd.getName() + " to " + previous.withColor());
	}

}
