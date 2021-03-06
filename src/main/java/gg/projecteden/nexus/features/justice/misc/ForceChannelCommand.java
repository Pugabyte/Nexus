package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PublicChannel;
import lombok.NonNull;

@Aliases("fc")
@Permission("group.staff")
public class ForceChannelCommand extends CustomCommand {

	public ForceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <channel>")
	void forceChannel(Chatter chatter, PublicChannel channel) {
		chatter.setActiveChannel(channel, true);
		send("&3Forced &e" + chatter.getOfflinePlayer().getName() + " &3to " + channel.getColor() + channel.getName());
	}

}
