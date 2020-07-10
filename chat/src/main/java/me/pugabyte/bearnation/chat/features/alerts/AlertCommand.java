package me.pugabyte.bearnation.chat.features.alerts;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.SoundUtils.Jingle;
import org.bukkit.entity.Player;

public class AlertCommand extends CustomCommand {

	public AlertCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alert(Player player) {
		Jingle.PING.play(player);
	}

}
