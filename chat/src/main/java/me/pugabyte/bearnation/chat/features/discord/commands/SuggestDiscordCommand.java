package me.pugabyte.bearnation.chat.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bearnation.api.framework.exceptions.BNException;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.models.nerd.NerdService;
import me.pugabyte.bearnation.api.models.nerd.Rank;
import me.pugabyte.bearnation.api.utils.RandomUtils;
import me.pugabyte.bearnation.api.utils.StringUtils.TimespanFormatter;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.discord.Bot;
import me.pugabyte.bearnation.chat.features.discord.Bot.HandledBy;
import me.pugabyte.bearnation.chat.features.discord.DiscordId;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Role;
import me.pugabyte.bearnation.server.models.hours.Hours;
import me.pugabyte.bearnation.server.models.hours.HoursService;
import me.pugabyte.bearnation.server.models.litebans.LiteBansService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static me.pugabyte.bearnation.api.utils.StringUtils.camelCase;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

@HandledBy(Bot.RELAY)
public class SuggestDiscordCommand extends Command {

	public SuggestDiscordCommand() {
		this.name = "suggest";
		this.requiredRole = Role.STAFF.name();
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		if (!event.getChannel().getId().equals(DiscordId.Channel.STAFF_PROMOTIONS.getId()))
			return;

		Chat.tasks().async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/suggest <player>`");

				Nerd nerd = new NerdService(Chat.inst()).get(Utils.getPlayer(args[0]));
				if (!Arrays.asList(Rank.MEMBER, Rank.TRUSTED).contains(nerd.getRank()))
					throw new InvalidInputException(nerd.getName() + " is not eligible for promotion (They are " + nerd.getRank().plain() + ")");

				Hours hours = new HoursService(Chat.inst()).get(nerd.getOfflinePlayer());

				String firstJoin = nerd.getFirstJoin().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
				String hoursTotal = TimespanFormatter.of(hours.getTotal()).noneDisplay(true).format();
				String hoursMonthly = TimespanFormatter.of(hours.getMonthly()).noneDisplay(true).format();
				String history = "None";
				if (new LiteBansService(Chat.inst()).getHistory(nerd.getUuid()) > 0)
					 history = "[View](https://bans.bnn.gg/history.php?uuid=" + nerd.getUuid() + ")";

				EmbedBuilder embed = new EmbedBuilder()
						.appendDescription("\n:calendar_spiral: **First join**: " + firstJoin)
						.appendDescription("\n:clock" + RandomUtils.randomInt(1, 12) + ": **Hours (Total)**: " + hoursTotal)
						.appendDescription("\n:clock" + RandomUtils.randomInt(1, 12) + ": **Hours (Monthly)**: " + hoursMonthly)
						.appendDescription("\n:scroll: **History**: " + history)
						.setThumbnail("https://minotar.net/helm/" + nerd.getName() + "/100.png");

				Rank next = nerd.getRank().next();
				embed.setColor(next.getColor());

				event.reply(new MessageBuilder()
						.setContent(event.getAuthor().getAsMention() + " is suggesting **" + nerd.getName() + "** for **" + camelCase(next.plain()) + "**")
						.setEmbed(embed.build())
						.build());
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

}
