package me.pugabyte.bearnation.survival.features.mcmmo;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.models.mcmmo.McMMOPrestige;
import me.pugabyte.bearnation.models.mcmmo.McMMOService;
import org.bukkit.OfflinePlayer;

public class McMMOPrestigeCommand extends CustomCommand {
	private McMMOService service = new McMMOService();

	public McMMOPrestigeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void main(@Arg("self") OfflinePlayer player) {
		McMMOPrestige mcMMOPrestige = service.getPrestige(player.getUniqueId().toString());

		line();
		send("Prestige for " + player.getName());
		mcMMOPrestige.getPrestiges().forEach((type, count) -> send(camelCase(type) + ": " + count));

	}

}
