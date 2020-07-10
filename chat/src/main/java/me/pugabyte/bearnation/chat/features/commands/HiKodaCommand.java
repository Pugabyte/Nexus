package me.pugabyte.bearnation.chat.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Time;

@Cooldown(@Part(Time.MINUTE))
public class HiKodaCommand extends CustomCommand {

	public HiKodaCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void hiKoda() {
		runCommand("ch qm g Hi Koda!");
	}

}
