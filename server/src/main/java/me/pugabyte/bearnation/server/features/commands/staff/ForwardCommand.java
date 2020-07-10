package me.pugabyte.bearnation.server.features.commands.staff;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.util.Vector;

@Permission("group.staff")
public class ForwardCommand extends CustomCommand {

	public ForwardCommand(CommandEvent event) {
		super(event);
	}

	@Path("<blocks>")
	void forward(int blocks) {
		Vector forward = player().getEyeLocation().getDirection().multiply(blocks);
		player().teleport(player().getLocation().add(forward));
	}
}
