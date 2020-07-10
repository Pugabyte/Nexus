package me.pugabyte.bearnation.chat.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bearnation.api.framework.exceptions.BNException;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.discord.Bot;
import me.pugabyte.bearnation.chat.features.discord.Bot.HandledBy;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Role;
import me.pugabyte.bearnation.chat.models.discord.DiscordService;
import me.pugabyte.bearnation.chat.models.discord.DiscordUser;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class BalanceDiscordCommand extends Command {

	public BalanceDiscordCommand() {
		this.name = "balance";
		this.aliases = new String[]{"bal", "money"};
	}

	protected void execute(CommandEvent event) {
		Chat.tasks().async(() -> {
			try {
				if (!event.getMember().getRoles().contains(Role.VERIFIED.get()))
					throw new InvalidInputException("You must link your Discord and Minecraft accounts before using this command");

				DiscordUser user = new DiscordService(Chat.inst()).getFromUserId(event.getAuthor().getId());
				OfflinePlayer player = Utils.getPlayer(user.getUuid());

				String[] args = event.getArgs().split(" ");
				if (args.length > 0 && !isNullOrEmpty(args[0]))
					player = Utils.getPlayer(args[0]);

				String formatted = NumberFormat.getCurrencyInstance().format(Chat.inst().getEcon().getBalance(player));
				boolean isSelf = user.getUuid().equals(player.getUniqueId().toString());
				event.reply("Balance" + (isSelf ? "" : " of " + player.getName()) + ": " + formatted);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
