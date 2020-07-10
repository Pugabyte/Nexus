package me.pugabyte.bearnation.server.features.holidays.bearfair20.quests.arcademachine;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Permission("group.admin")
public class BFArcadeCommand extends CustomCommand {

	public BFArcadeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void open() {
		new ArcadeMachineMenu().open(player(), null);
	}

}
