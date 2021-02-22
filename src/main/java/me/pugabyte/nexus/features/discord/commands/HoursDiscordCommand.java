package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import joptsimple.internal.Strings;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.hours.Hours;
import me.pugabyte.nexus.models.hours.HoursService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatter;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;

import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class HoursDiscordCommand extends Command {

	public HoursDiscordCommand() {
		this.name = "hours";
		this.aliases = new String[]{"playtime", "days", "minutes", "seconds"};
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			try {
				OfflinePlayer player;

				String[] args = event.getArgs().split(" ");
				Nexus.log(Arrays.toString(args));
				if (args.length > 0 && !Strings.isNullOrEmpty(args[0]))
					player = PlayerUtils.getPlayer(args[0]);
				else
					try {
						DiscordUser user = new DiscordService().checkVerified(event.getAuthor().getId());
						Nexus.log(user.toString());
						player = user.getOfflinePlayer();
					} catch (InvalidInputException ex) {
						throw new InvalidInputException("You must either link your Discord and Minecraft accounts or supply a name");
					}

				HoursService service = new HoursService();
				Hours hours = service.get(player);

				String message = "**[Hours]** " + hours.getName() + "'s in-game playtime";
				message += System.lineSeparator() + "Total: **" + TimespanFormatter.of(hours.getTotal()).noneDisplay(true).format() + "**";
				message += System.lineSeparator() + "- Today: **" + TimespanFormatter.of(hours.getDaily()).noneDisplay(true).format() + "**";
				message += System.lineSeparator() + "- This month: **" + TimespanFormatter.of(hours.getMonthly()).noneDisplay(true).format() + "**";
				message += System.lineSeparator() + "- This year: **" + TimespanFormatter.of(hours.getYearly()).noneDisplay(true).format() + "**";

				event.reply(message);
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}