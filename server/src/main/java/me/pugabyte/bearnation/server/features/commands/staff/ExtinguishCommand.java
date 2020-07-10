package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases("ext")
@Permission("group.staff")
public class ExtinguishCommand extends CustomCommand {

	public ExtinguishCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		player.setFireTicks(0);
	}

}
