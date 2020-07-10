package me.pugabyte.bearnation.server.features.warps.commands;

import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.warps.WarpType;
import me.pugabyte.bearnation.features.warps.WarpMenu;
import me.pugabyte.bearnation.features.warps.WarpsMenu;

@Redirect(from = "/survival", to = "/warp survival")
@Redirect(from = "/plaza", to = "/warp plaza")
@Redirect(from = "/mall", to = "/warp mall")
@Redirect(from = {"/shub", "/shophub"}, to = "/warp shub")
@Redirect(from = "/creative", to = "/warp creative")
@Redirect(from = "/skyblock", to = "/warp skyblock")
@Redirect(from = {"/minigames", "/gamelobby", "/gl"}, to = "/warp minigames")
@Aliases({"warp", "go", "goto", "hub", "tphub", "server", "servers", "lobby"})
public class WarpsCommand extends _WarpCommand {

	public WarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.NORMAL;
	}

	@Path
	void menu() {
		WarpsMenu.open(player(), WarpMenu.MAIN);
	}

}
