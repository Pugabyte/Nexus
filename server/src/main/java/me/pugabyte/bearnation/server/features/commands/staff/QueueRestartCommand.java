package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.features.afk.AFK;

@Permission("group.seniorstaff")
public class QueueRestartCommand extends CustomCommand {
	private static boolean restart = false;

	public QueueRestartCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(15), () -> {
			if (restart && AFK.getActivePlayers() == 0) {
				BNPlugin.log("Restart is queued");
				Tasks.wait(30 * 20, () -> {
					if (restart && AFK.getActivePlayers() == 0)
						Utils.runCommandAsConsole("inject plugins/wget/restart.sh");
				});
			}
		});
	}

	@Path("<true|false>")
	void toggle(boolean enable) {
		restart = enable;
		if (restart)
			send("&cRestart queued");
		else
			send("&eRestart not queued");
	}

}
