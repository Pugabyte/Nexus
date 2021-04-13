package me.pugabyte.nexus.features.discord.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.discord.Bot;
import me.pugabyte.nexus.features.discord.Bot.HandledBy;
import me.pugabyte.nexus.features.discord.DiscordId.TextChannel;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nickname.Nickname;
import me.pugabyte.nexus.models.nickname.Nickname.NicknameHistoryEntry;
import me.pugabyte.nexus.models.nickname.NicknameService;
import me.pugabyte.nexus.utils.PlayerUtils.Dev;
import me.pugabyte.nexus.utils.Tasks;

import java.util.Arrays;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@HandledBy(Bot.KODA)
public class NicknameDiscordCommand extends Command {

	public NicknameDiscordCommand() {
		this.name = "nickname";
		this.guildOnly = true;
		this.requiredRole = "Staff";
	}

	protected void execute(CommandEvent event) {
		Tasks.async(() -> {
			Nexus.log("Handling nickname discord command");
			try {
				if (!event.getChannel().getId().equals(TextChannel.STAFF_NICKNAME_QUEUE.getId()))
					throw new InvalidInputException("This command can only be used in #nickname-queue");

				String[] args = event.getArgs().split(" ");

				if (args.length >= 1)
					switch (args[0].toLowerCase()) {
						case "deny":
							Nexus.log("Denying");
							if (event.getMessage().getReferencedMessage() == null)
								throw new InvalidInputException("You must reply to the original message");

							Nerd nerd = Dev.PUGA.getNerd();
							Nickname nickname = new NicknameService().get(nerd);
							for (NicknameHistoryEntry entry : nickname.getNicknameHistory()) { // TODO query for correct player
								if (!event.getMessage().getReferencedMessage().getId().equals(entry.getNicknameQueueId()))
									if (event.getMessage().getReferencedMessage().getReferencedMessage() == null ||
											!event.getMessage().getReferencedMessage().getReferencedMessage().getId().equals(entry.getNicknameQueueId()))
									continue;

								entry.deny(String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
								event.getMessage().reply("Successfully updated reason").queue();
								new NicknameService().save(nickname);
							}
					}
			} catch (Exception ex) {
				event.reply(stripColor(ex.getMessage()));
				if (!(ex instanceof NexusException))
					ex.printStackTrace();
			}
		});
	}

}