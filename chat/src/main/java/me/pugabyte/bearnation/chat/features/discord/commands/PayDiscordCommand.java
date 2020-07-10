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
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.text.NumberFormat;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.bearnation.api.utils.StringUtils.colorize;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class PayDiscordCommand extends Command {

	public PayDiscordCommand() {
		this.name = "pay";
	}

	protected void execute(CommandEvent event) {
		Chat.tasks().async(() -> {
			try {
				if (!event.getMember().getRoles().contains(Role.VERIFIED.get()))
					throw new InvalidInputException("You must link your Discord and Minecraft accounts before using this command");

				String[] args = event.getArgs().split(" ");
				if (args.length != 2 || !Utils.isDouble(args[1]))
					throw new InvalidInputException("Correct usage: `/pay <player> <amount>`");

				DiscordUser user = new DiscordService(Chat.inst()).getFromUserId(event.getAuthor().getId());
				OfflinePlayer player = Utils.getPlayer(user.getUuid());
				OfflinePlayer target = Utils.getPlayer(args[0]);
				double amount = Double.parseDouble(args[1]);

				if (player.getUniqueId().equals(target.getUniqueId()))
					throw new InvalidInputException("You cannot pay yourself");

				if (amount < 0)
					throw new InvalidInputException("Amount must be greater than $0");

				EconomyResponse withdrawal = Chat.inst().getEcon().withdrawPlayer(player, amount);
				if (!withdrawal.transactionSuccess())
					throw new InvalidInputException("You do not have enough money to complete this transaction ("
							+ NumberFormat.getCurrencyInstance().format(Chat.inst().getEcon().getBalance(player)) + ")");

				EconomyResponse deposit = Chat.inst().getEcon().depositPlayer(target, amount);
				if (!deposit.transactionSuccess())
					if (!isNullOrEmpty(deposit.errorMessage))
						throw new InvalidInputException(deposit.errorMessage);
					else
						throw new InvalidInputException("Transaction was not successful");

				String formatted = NumberFormat.getCurrencyInstance().format(amount);
				if (target.isOnline() && target.getPlayer() != null)
					target.getPlayer().sendMessage(colorize("&a" + formatted + " has been received from " + player.getName()));

				event.reply("Successfully sent " + formatted + " to " + target.getName());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
