package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Aliases("tl")
@Permission("group.staff")
public class TimelockCommand extends CustomCommand {

	public TimelockCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("reset")
	void reset() {
		runCommandAsOp("mv gamerule doDaylightCycle true");
		send(PREFIX + "Normal daylight cycle resumed");
	}

	@Path("<time...>")
	void set(String time) {
		runCommandAsOp("time set " + time);
		runCommandAsOp("mv gamerule doDaylightCycle false");
		send(PREFIX + "Daylight cycle locked");
	}

}
