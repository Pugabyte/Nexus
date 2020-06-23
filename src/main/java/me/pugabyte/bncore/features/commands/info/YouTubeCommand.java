package me.pugabyte.bncore.features.commands.info;

import me.pugabyte.bncore.features.commands.info.SocialMediaCommand.SocialMedia;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

@Aliases("yt")
public class YouTubeCommand extends CustomCommand {

	public YouTubeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().urlize("&e" + SocialMedia.YOUTUBE.getUrl()));
	}

}
