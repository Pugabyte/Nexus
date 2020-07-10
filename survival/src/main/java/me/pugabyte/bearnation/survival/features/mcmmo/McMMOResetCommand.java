package me.pugabyte.bearnation.survival.features.mcmmo;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Redirects.Redirect;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldGroup;
import me.pugabyte.bearnation.features.mcmmo.menus.McMMOResetMenu;

@Redirect(from = "/mcmmo reset", to = "/mcmmoreset")
public class McMMOResetCommand extends CustomCommand {
	public McMMOResetCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void McMMOReset() {
		if (WorldGroup.get(player()) != WorldGroup.SURVIVAL)
			error("You cannot use this outside of survival");

		McMMOResetMenu.openMcMMOReset(player());
	}
}
