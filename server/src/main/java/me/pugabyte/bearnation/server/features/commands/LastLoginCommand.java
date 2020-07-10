package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.utils.StringUtils;

@Aliases("lastjoin")
public class LastLoginCommand extends CustomCommand {

	public LastLoginCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void lastLogin(@Arg("self") Nerd nerd) {
		send("&e&l" + nerd.getName() + " &3last logged in &e" + StringUtils.timespanDiff(nerd.getLastJoin()) + " &3ago");
	}
}
