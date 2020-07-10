package me.pugabyte.bearnation.server.features.commands.staff;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.models.nerd.Rank;
import me.pugabyte.bearnation.api.utils.SoundUtils.Jingle;

@Permission("group.seniorstaff")
public class PromoteCommand extends CustomCommand {

	public PromoteCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void promote(Nerd nerd) {
		Rank rank = nerd.getRank();
		Rank next = rank.next();
		if (rank == next)
			error("User is already max rank");

		runCommandAsConsole("lp user " + nerd.getName() + " parent set " + next.name());
		send(PREFIX + "Promoted " + nerd.getName() + " to " + next.withColor());

		if (nerd.getOfflinePlayer().isOnline())
			Jingle.RANKUP.play(nerd.getPlayer());
	}

}
