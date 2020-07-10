package me.pugabyte.bearnation.survival.features.commands.staff;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

public class LWCCOCommand extends CustomCommand {

	public LWCCOCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(OfflinePlayer player) {
		runCommand("lwc admin forceowner " + player.getName());
	}
}
