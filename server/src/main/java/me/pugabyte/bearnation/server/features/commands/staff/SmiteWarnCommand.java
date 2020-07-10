package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.features.minigames.Minigames;
import org.bukkit.entity.Player;

@Permission("group.moderator")
public class SmiteWarnCommand extends CustomCommand {

	public SmiteWarnCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void smiteWarn(Player player) {
		if (!Minigames.isMinigameWorld(player.getWorld()))
			error("Target player is not in minigames");

		player.getWorld().strikeLightningEffect(player.getLocation());
		runCommand("warn " + player.getName() + " Please obey the rules of our minigames.");
	}


}
