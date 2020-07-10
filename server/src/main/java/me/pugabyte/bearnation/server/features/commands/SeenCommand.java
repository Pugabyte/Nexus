package me.pugabyte.bearnation.server.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.utils.StringUtils;

public class SeenCommand extends CustomCommand {

	public SeenCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	public void seen(Nerd nerd) {
		if (nerd.getOfflinePlayer().isOnline())
			send(PREFIX + "&e" + nerd.getName() + " &3has been &aonline &3for &e" + StringUtils.timespanDiff(nerd.getLastJoin()));
		else
			send(PREFIX + "&e" + nerd.getName() + " &3has been &coffline &3for &e" + StringUtils.timespanDiff(nerd.getLastQuit()));
	}
}
