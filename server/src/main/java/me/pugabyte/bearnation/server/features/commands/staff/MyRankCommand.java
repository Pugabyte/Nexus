package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Rank;

@Permission("set.my.rank")
public class MyRankCommand extends CustomCommand {

	public MyRankCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<rank>")
	void set(Rank rank) {
		runCommandAsConsole("lp user " + player().getName() + " parent set " + rank.name());
		send(PREFIX + "Set your rank to " + rank.withColor());
	}

}
