package me.pugabyte.bearnation.server.features.socialmedia;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.server.models.socialmedia.SocialMediaUser.BNSocialMediaSite;

public class RedditCommand extends CustomCommand {

	public RedditCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().urlize("&e" + BNSocialMediaSite.REDDIT.getUrl()));
	}

}