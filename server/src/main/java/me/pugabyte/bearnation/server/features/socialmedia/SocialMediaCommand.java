package me.pugabyte.bearnation.server.features.socialmedia;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.server.models.socialmedia.SocialMediaService;
import me.pugabyte.bearnation.server.models.socialmedia.SocialMediaUser.BNSocialMediaSite;
import me.pugabyte.bearnation.server.models.socialmedia.SocialMediaUser.SocialMediaSite;

public class SocialMediaCommand extends CustomCommand {
	private final SocialMediaService service = new SocialMediaService();

	public SocialMediaCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		line();
		for (BNSocialMediaSite site : BNSocialMediaSite.values())
			send(json().urlize(site.getName() + " &7- &e" + site.getUrl()));
	}

	@Path("reload")
	@Permission("group.admin")
	void reload() {
		SocialMediaSite.reload();
		send(PREFIX + "Reloaded");
	}

	@Path("getItem <site>")
	@Permission("group.admin")
	void getItem(SocialMediaSite site) {
		Utils.giveItem(player(), site.getHead());
	}

}
