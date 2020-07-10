package me.pugabyte.bearnation.server.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Fallback;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldGroup;

@Fallback("playervaults")
@Aliases({"pv", "chest", "vault"})
public class PlayerVaultsCommand extends CustomCommand {

	public PlayerVaultsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		if (WorldGroup.get(player()) != WorldGroup.SURVIVAL && !player().hasPermission("group.seniorstaff"))
			error("You can't open vaults here");
		fallback();
	}

}
