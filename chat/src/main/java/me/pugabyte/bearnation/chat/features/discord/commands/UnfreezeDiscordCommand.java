package me.pugabyte.bearnation.chat.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bearnation.api.framework.exceptions.BNException;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.bearnation.api.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.Chat.StaticChannel;
import me.pugabyte.bearnation.chat.features.discord.Bot;
import me.pugabyte.bearnation.chat.features.discord.Bot.HandledBy;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Channel;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Role;
import me.pugabyte.bearnation.chat.models.discord.DiscordService;
import me.pugabyte.bearnation.chat.models.discord.DiscordUser;
import me.pugabyte.bearnation.server.models.freeze.Freeze;
import me.pugabyte.bearnation.server.models.freeze.FreezeService;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.bearnation.api.utils.StringUtils.colorize;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class UnfreezeDiscordCommand extends Command {
	public static final String PREFIX = StringUtils.getPrefix("Freeze");

	public UnfreezeDiscordCommand() {
		this.name = "unfreeze";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Chat.tasks().sync(() -> {
			try {
				if (Strings.isNullOrEmpty(event.getArgs()))
					throw new InvalidInputException("Correct usage: /unfreeze <players...>");

				DiscordUser user = new DiscordService(Chat.inst()).getFromUserId(event.getAuthor().getId());
				if (user.getUuid() == null)
					throw new NoPermissionException();

				FreezeService service = new FreezeService(Chat.inst());
				OfflinePlayer executor = Utils.getPlayer(user.getUuid());

				for (String arg : event.getArgs().split(" ")) {
					try {
						OfflinePlayer player = Utils.getPlayer(arg);
						if (!player.isOnline() || player.getPlayer() == null)
							throw new PlayerNotOnlineException(player);

						Freeze freeze = service.get(player);
						if (!freeze.isFrozen())
							throw new InvalidInputException(player.getName() + " is not frozen");

						freeze.setFrozen(false);
						service.save(freeze);

						if (player.getPlayer().getVehicle() != null)
							player.getPlayer().getVehicle().remove();

						player.getPlayer().sendMessage(colorize("&cYou have been unfrozen."));
						Chat.broadcast(PREFIX + "&e" + executor.getName() + " &3has unfrozen &e" + player.getName(), StaticChannel.STAFF);
					} catch (Exception ex) {
						event.reply(stripColor(ex.getMessage()));
						if (!(ex instanceof BNException))
							ex.printStackTrace();
					}
				}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}


}
