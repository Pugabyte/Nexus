package me.pugabyte.bearnation.server.features.store.perks.fireworks;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.FireworkLauncher;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time;

@Aliases("multifw")
@Permission("firework.launch")
@Cooldown(value = @Part(value = Time.SECOND, x = 10), bypass = "group.staff")
public class MultiFireworkCommand extends CustomCommand {

	public MultiFireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void firework() {
		Tasks.Countdown.builder()
				.duration(Time.SECOND.x(20))
				.onSecond(i -> {
					if (i % 2 == 0)
						FireworkLauncher.random(player().getLocation()).launch();
				})
				.doZero(true)
				.start();
	}
}
