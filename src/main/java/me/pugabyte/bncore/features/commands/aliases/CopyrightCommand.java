package me.pugabyte.bncore.features.commands.aliases;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class CopyrightCommand extends CustomCommand {

	public CopyrightCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		runCommandAsConsole("essentials:sudo " + player().getName() + " c:" + argsString() + "©");
	}

}
