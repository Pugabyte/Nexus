package me.pugabyte.nexus.features.commands.info;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;

public class ApplyCommand extends CustomCommand {

	public ApplyCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		send("&3Ready to apply for &b&oModerator&3?");
		send("&3How does your name look in blue, &b&o" + player().getName() + "&3? :)");
		send("&3If you think you are ready for this position, you can fill out an application here:");
		send(json().next("&ehttps://bnn.gg/apply/mod"));
	}

}