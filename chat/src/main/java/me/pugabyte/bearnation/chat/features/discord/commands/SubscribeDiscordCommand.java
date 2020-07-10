package me.pugabyte.bearnation.chat.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.bearnation.api.framework.exceptions.BNException;
import me.pugabyte.bearnation.api.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.discord.Bot;
import me.pugabyte.bearnation.chat.features.discord.Bot.HandledBy;
import me.pugabyte.bearnation.chat.features.discord.Discord;
import me.pugabyte.bearnation.chat.features.discord.DiscordId;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Role;

import static me.pugabyte.bearnation.api.utils.StringUtils.camelCase;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class SubscribeDiscordCommand extends Command {

	public SubscribeDiscordCommand() {
		this.name = "subscribe";
		this.guildOnly = true;
	}

	protected void execute(CommandEvent event) {
		Chat.tasks().async(() -> {
			try {
				String[] args = event.getArgs().split(" ");
				if (args.length == 0)
					throw new InvalidInputException("Correct usage: `/subscribe <role>`");

				Role role = getRole(args[0]);
				if (role == null)
					throw new InvalidInputException("Unknown role, available options are `minigames` and `movienight`");

				Discord.addRole(event.getAuthor().getId(), role);
				event.reply(event.getAuthor().getAsMention() + " You have subscribed to " + camelCase(role.name()));
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof BNException))
					ex.printStackTrace();
			}
		});
	}

	static DiscordId.Role getRole(String input) {
		switch (input) {
			case "minigames":
			case "minigame":
			case "minigamesnews":
			case "minigamenews":
				return Role.MINIGAME_NEWS;
			case "movienight":
			case "theatre":
			case "moviegoer":
			case "moviegoers":
				return Role.MOVIE_GOERS;
		}

		return null;
	}

}
