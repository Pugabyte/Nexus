package gg.projecteden.nexus.features.warps.commands;

import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.warps.WarpType;

@Aliases("arena")
@Permission("group.staff")
public class ArenasCommand extends _WarpCommand {

	public ArenasCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.ARENA;
	}

}
