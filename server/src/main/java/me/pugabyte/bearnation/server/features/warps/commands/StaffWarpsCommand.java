package me.pugabyte.bearnation.server.features.warps.commands;

import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.warps.WarpType;

@Aliases("staffwarp")
@Permission("group.staff")
public class StaffWarpsCommand extends _WarpCommand {

	public StaffWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.STAFF;
	}

}
