package me.pugabyte.nexus.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Permission("worldedit.navigation.jumpto")
public class JumpCommand extends CustomCommand {

	public JumpCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		Block target = player().getTargetBlockExact(500);
		if (target == null)
			error("You must be looking at a block");

		player().teleport(target.getLocation().add(0, 1, 0), TeleportCause.COMMAND);

	}

}