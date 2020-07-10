package me.pugabyte.bearnation.server.features.socialmedia;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.server.models.socialmedia.SocialMediaUser.BNSocialMediaSite;

@Aliases("yt")
public class YouTubeCommand extends CustomCommand {

	public YouTubeCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().urlize("&e" + BNSocialMediaSite.YOUTUBE.getUrl()));
	}

}
