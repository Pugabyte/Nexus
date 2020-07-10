package me.pugabyte.bearnation.server.features.warps.commands;

import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.warps.WarpType;

@Aliases("xraywarp")
@Permission("group.staff")
public class XrayWarpsCommand extends _WarpCommand {

	public XrayWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.XRAY;
	}
}
