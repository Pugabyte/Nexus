package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.features.minigames.Minigames;
import org.bukkit.entity.Player;

public class DistanceCommand extends CustomCommand {

	public DistanceCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void distance(Player target) {
		if (Minigames.isMinigameWorld(player().getWorld()))
			error("You can't use that here, that's cheating!");

		if (!player().getWorld().equals(target.getWorld()))
			error("Player is not in the same world.");

		if (Utils.isVanished(target) && !player().hasPermission("pv.see"))
			throw new PlayerNotOnlineException(target);

		send(PREFIX + player().getLocation().distance(target.getLocation()) + " blocks.");
	}
}
