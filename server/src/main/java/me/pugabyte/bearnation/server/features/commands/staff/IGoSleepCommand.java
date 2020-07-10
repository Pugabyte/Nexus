package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time;

@Permission("group.staff")
public class IGoSleepCommand extends CustomCommand {

	public IGoSleepCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[hours]")
	void run(@Arg("4") int hours) {
		send("Kicking you in " + hours + " hours");
		Tasks.wait(Time.HOUR.x(hours), () -> player().kickPlayer("Goodnight"));
	}

}
