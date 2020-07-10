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
public class ExpandVCommand extends CustomCommand {

	public ExpandVCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void expandV(@Arg("1") int amount) {
		new WorldEditUtils(player()).changeSelection(
				player(),
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.VERTICAL,
				amount
		);
	}

}

