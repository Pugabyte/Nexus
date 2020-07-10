package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class FeedCommand extends CustomCommand {

	public FeedCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		player.setFoodLevel(20);
		player.setSaturation(10);
		player.setExhaustion(0);
		send(PREFIX + "Fed " + player.getName());
	}

}
