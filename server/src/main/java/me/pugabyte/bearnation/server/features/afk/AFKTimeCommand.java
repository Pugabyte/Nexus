package me.pugabyte.bearnation.server.features.afk;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.StringUtils;
import org.bukkit.entity.Player;

@Aliases("timeafk")
public class AFKTimeCommand extends CustomCommand {

	public AFKTimeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void timeAfk(@Arg("self") Player player) {
		String timespan = StringUtils.timespanDiff(AFK.get(player).getTime());
		send("&3" + player.getName() + " has been AFK for &e" + timespan);
	}

}
