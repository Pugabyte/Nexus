package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.utils.Utils;
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