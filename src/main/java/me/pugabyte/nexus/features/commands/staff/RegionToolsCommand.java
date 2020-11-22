package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGuardUtils;

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
		wgUtils.getRegionsLikeAt(filter, player().getLocation()).forEach(region -> send(region.getId()));
	}

}