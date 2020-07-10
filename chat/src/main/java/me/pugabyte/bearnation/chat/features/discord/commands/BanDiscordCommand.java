package me.pugabyte.bearnation.chat.features.discord.commands;

import com.google.common.base.Strings;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.discord.Bot;
import me.pugabyte.bearnation.chat.features.discord.Bot.HandledBy;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Channel;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Role;
import me.pugabyte.bearnation.chat.models.discord.DiscordService;
import me.pugabyte.bearnation.chat.models.discord.DiscordUser;

import static me.pugabyte.bearnation.api.utils.StringUtils.trimFirst;
import static me.pugabyte.bearnation.api.utils.Utils.runCommandAsConsole;

@HandledBy(Bot.RELAY)
public class BanDiscordCommand extends Command {

	public BanDiscordCommand() {
		this.name = "ban";
		this.aliases = new String[]{"tempban", "unban", "banip", "ipban", "kick", "warn", "unwarn", "mute", "unmute"};
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(Channel.STAFF_BRIDGE.getId()))
			return;

		Chat.tasks().async(() -> {
			DiscordUser user = new DiscordService(Chat.inst()).getFromUserId(event.getAuthor().getId());
			if (!Strings.isNullOrEmpty(user.getUserId()))
				Chat.tasks().sync(() ->
						runCommandAsConsole(trimFirst(event.getMessage().getContentRaw() + " --sender=" + Utils.getPlayer(user.getUuid()).getName())));
		});
	}


}
