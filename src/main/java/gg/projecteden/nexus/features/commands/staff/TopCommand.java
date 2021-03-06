package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

@Permission("essentials.top")
public class TopCommand extends CustomCommand {

	public TopCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[y]")
	void run(Integer y) {
		if (y == null)
			y = world().getHighestBlockYAt(location()) + 1;
		Location top = location().clone();
		top.setY(y);
		top.setYaw(location().getYaw());
		top.setPitch(location().getPitch());
		player().teleport(top, TeleportCause.COMMAND);
		send(PREFIX + "Teleported to top");
	}

}
