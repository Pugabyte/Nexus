package me.pugabyte.bearnation.server.features.commands.info;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldGroup;
import me.pugabyte.bearnation.features.scoreboard.ScoreboardLine;
import org.bukkit.entity.Player;

@Aliases("whatworld")
public class WorldCommand extends CustomCommand {

	public WorldCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Player player) {
		String render = ScoreboardLine.WORLD.render(player).split(":")[1].trim();
		send("&3" + (isSelf(player) ? "You are" : player.getName() + " is") + " in world &e" + render + " &3in group &e" + WorldGroup.get(player));
	}

}
