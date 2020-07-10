package me.pugabyte.bearnation.server.features.store.perks.fireworks;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.FireworkLauncher;
import me.pugabyte.bearnation.api.utils.Time;

@Aliases("fw")
@Permission("firework.launch")
@Cooldown(value = @Part(Time.SECOND), bypass = "group.staff")
public class FireworkCommand extends CustomCommand {

	public FireworkCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void firework() {
		FireworkLauncher.random(player().getLocation()).launch();
	}
}
