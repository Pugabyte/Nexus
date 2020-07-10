package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

public class HungerCommand extends CustomCommand {

	public HungerCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player] [number]")
	void hunger(@Arg("self") Player player, Integer hunger) {
		if (hunger == null)
			send(PREFIX + stripColor(player.getName()) + "'s hunger is " + player.getFoodLevel());
		else {
			checkPermission("hunger.set");
			player.setFoodLevel(hunger);
			send(PREFIX + stripColor(player.getName()) + "'s hunger set to " + player.getFoodLevel());
		}
	}

	@Path("target [number]")
	void target(Integer hunger) {
		Entity entity = Utils.getTargetEntity(player());
		if (!(entity instanceof Player))
			error("Only players have hunger");

		hunger((Player) entity, hunger);
	}
}
