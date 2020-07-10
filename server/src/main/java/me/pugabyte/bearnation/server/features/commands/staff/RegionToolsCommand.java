package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldGuardUtils;

@Permission("group.staff")
public class RegionToolsCommand extends CustomCommand {
	WorldGuardUtils wgUtils;

	public RegionToolsCommand(@NonNull CommandEvent event) {
		super(event);
		wgUtils = new WorldGuardUtils(player());
	}

	@Path("getRegionsLikeAt <filter>")
	void getRegionsLikeAt(String filter) {
		send(PREFIX + "Found regions:");
		wgUtils.getRegionsLikeAt(player().getLocation(), filter).forEach(region -> send(region.getId()));
	}

}
