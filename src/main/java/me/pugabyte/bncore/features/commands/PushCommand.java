package me.pugabyte.bncore.features.commands;

import lombok.Getter;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;

public class PushCommand extends CustomCommand {
	@Getter
	private static String perm = "stoppushing.allow";

	static {
		BNCore.registerPlaceholder("pushing", event ->
				String.valueOf(event.getPlayer().hasPermission(perm)));
	}

	public PushCommand(CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null)
			push(!player().hasPermission(perm));
		else
			push(enable);
	}

	@SneakyThrows
	void push(boolean enable) {
		if (enable) {
			runCommandAsConsole("lp user " + player().getName() + " permission set " + perm + " true");
			send("&ePushing will be turned &aon&e shortly.");
		} else {
			runCommandAsConsole("lp user " + player().getName() + " permission set " + perm + " false");
			send("&ePushing will be turned &coff&e shortly.");
		}
	}

}
