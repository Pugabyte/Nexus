package me.pugabyte.bearnation.creative.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

public class MakeDownloadCommand extends CustomCommand {

	public MakeDownloadCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void run(Player player) {
		runCommandAsOp(player, "plot download");
	}
}
