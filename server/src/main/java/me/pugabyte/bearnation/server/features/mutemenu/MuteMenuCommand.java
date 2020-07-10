package me.pugabyte.bearnation.server.features.mutemenu;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class MuteMenuCommand extends CustomCommand {

	public MuteMenuCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void muteMenu() {
		send(PREFIX + "&3The mute menu is down for &oMaintenance. &cSorry for the inconvenience.");
//		SmartInventory INV = SmartInventory.builder()
//				.title("&3Mute Menu")
//				.size(6, 9)
//				.provider(new MuteMenuProvider())
//				.build();
//		INV.open(player());
	}

}
