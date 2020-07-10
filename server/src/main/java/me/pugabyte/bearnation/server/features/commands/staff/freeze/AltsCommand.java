package me.pugabyte.bearnation.server.features.commands.staff.freeze;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

public class AltsCommand extends CustomCommand {

	public AltsCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(OfflinePlayer player) {
		runCommand("dupeip " + player.getName());
	}

}
