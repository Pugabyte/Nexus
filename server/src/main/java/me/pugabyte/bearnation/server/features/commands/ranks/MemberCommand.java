package me.pugabyte.bearnation.server.features.commands.ranks;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class MemberCommand extends CustomCommand {

	public MemberCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void member() {
		line(5);
		send("&fMember &3rank is achieved by playing for a total of &e24 hours&3. Use &c/hours &3to check your play time.");
		line();
		RanksCommand.ranksReturn(player());
	}
}
