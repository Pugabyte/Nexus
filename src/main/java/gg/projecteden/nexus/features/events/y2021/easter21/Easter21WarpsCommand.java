package gg.projecteden.nexus.features.events.y2021.easter21;

import gg.projecteden.nexus.features.warps.commands._WarpCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Permission("group.admin")
public class Easter21WarpsCommand extends _WarpCommand {

	public Easter21WarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.EASTER21;
	}

}
