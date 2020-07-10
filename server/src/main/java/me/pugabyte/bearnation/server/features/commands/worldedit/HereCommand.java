package me.pugabyte.bearnation.server.features.commands.worldedit;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldEditUtils;

@DoubleSlash
@Permission("worldedit.wand")
public class HereCommand extends CustomCommand {

	public HereCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void here(@Arg("0") int amount) {
		new WorldEditUtils(player()).setSelection(player(), player().getLocation());
		ExpandAllCommand.expandAll(player(), amount);
	}
}

