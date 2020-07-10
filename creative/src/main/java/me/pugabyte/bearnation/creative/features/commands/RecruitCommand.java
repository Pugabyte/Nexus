package me.pugabyte.bearnation.creative.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class RecruitCommand extends CustomCommand {

	public RecruitCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void recruit(Player player) {
		runCommand("rg addmember entry-deny " + player.getName() + " -w buildadmin");
		send(PREFIX + "Added &e" + player.getName());
	}

}
