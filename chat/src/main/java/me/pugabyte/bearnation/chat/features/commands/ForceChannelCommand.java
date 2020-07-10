package me.pugabyte.bearnation.chat.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.chat.models.chat.PublicChannel;

@Permission("group.staff")
public class ForceChannelCommand extends CustomCommand {

	public ForceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <channel>")
	void forceChannel(Chatter chatter, PublicChannel channel) {
		chatter.setActiveChannel(channel);
		send("&3Forced &e" + chatter.getOfflinePlayer().getName() + " &3to " + channel.getColor() + channel.getName());
	}

}
