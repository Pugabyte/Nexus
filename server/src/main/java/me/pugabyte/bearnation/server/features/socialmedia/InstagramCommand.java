package me.pugabyte.bearnation.server.features.socialmedia;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.server.models.socialmedia.SocialMediaUser.BNSocialMediaSite;

@Aliases("insta")
public class InstagramCommand extends CustomCommand {

	public InstagramCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		send(json().urlize("&e" + BNSocialMediaSite.INSTAGRAM.getUrl()));
	}

}
