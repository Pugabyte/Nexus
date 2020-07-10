package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases("skinifier")
public class SkinifyCommand extends CustomCommand {

	public SkinifyCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void skin(@Arg("self") OfflinePlayer player) {
		send(json("&eClick here &3to Bear Nation-ify your skin!").url("http://bnn.gg/skins/?uuid=" + player.getUniqueId()));
	}
}
