package me.pugabyte.bearnation.server.features.commands.staff;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Permission("group.staff")
public class FakeOpCommand extends CustomCommand {

	public FakeOpCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void fakeop(Player player) {
		player.sendMessage("Opped " + player.getName());
	}

}
