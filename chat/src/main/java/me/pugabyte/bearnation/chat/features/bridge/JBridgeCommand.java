package me.pugabyte.bearnation.chat.features.bridge;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.chat.models.discord.DiscordService;
import me.pugabyte.bearnation.chat.models.discord.DiscordUser;
import org.bukkit.OfflinePlayer;

public class JBridgeCommand extends CustomCommand {
	private DiscordService service = new DiscordService(getPlugin());

	public JBridgeCommand(CommandEvent event) {
		super(event);
		;
	}

	@Path("get <player>")
	void get(@Arg("self") OfflinePlayer player) {
		DiscordUser user = service.get(player);
		send("User: " + user);
	}

}
