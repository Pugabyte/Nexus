package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Permission("group.seniorstaff")
public class MoreCommand extends CustomCommand {

	public MoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		getToolRequired().setAmount(64);
	}

}
