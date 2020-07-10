package me.pugabyte.bearnation.server.features.warps.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.warps.Warp;
import me.pugabyte.bearnation.api.models.warps.WarpService;
import me.pugabyte.bearnation.api.models.warps.WarpType;
import me.pugabyte.bearnation.api.utils.WorldGroup;
import org.bukkit.entity.Player;

public class SpawnCommand extends CustomCommand {

	public SpawnCommand(CommandEvent event) {
		super(event);
	}

	WarpService service = new WarpService();

	@Path
	void run() {
		String warpName = "spawn";
		if (WorldGroup.get(player()) == WorldGroup.SKYBLOCK)
			warpName = "skyblock";
		else if (WorldGroup.get(player()) == WorldGroup.CREATIVE)
			warpName = "creative";

		Warp warp = service.get(warpName, WarpType.NORMAL);
		if (!warp.getName().equalsIgnoreCase("spawn"))
			send(json("&3If you want to go to the survival spawn, &eclick here.").suggest("/warp spawn"));
		warp.teleport(player());
	}

	@Path("[player]")
	@Permission("group.staff")
	void sudo(Player player) {
		runCommand(player, "spawn");
	}

}
