package me.pugabyte.bearnation.chat.features;

import lombok.NoArgsConstructor;
import me.pugabyte.bearnation.api.framework.commands.Commands;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.events.ChatEvent;
import me.pugabyte.bearnation.chat.features.events.DiscordChatEvent;
import me.pugabyte.bearnation.chat.features.events.MinecraftChatEvent;
import me.pugabyte.bearnation.chat.features.events.PublicChatEvent;
import me.pugabyte.bearnation.chat.models.chat.ChatService;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.chat.models.chat.PrivateChannel;
import me.pugabyte.bearnation.server.features.afk.AFK;
import me.pugabyte.bearnation.server.models.afk.AFKPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.regex.Pattern;

import static me.pugabyte.bearnation.api.utils.StringUtils.colorize;
import static me.pugabyte.bearnation.api.utils.StringUtils.right;
import static me.pugabyte.bearnation.api.utils.Utils.runCommand;

@NoArgsConstructor
public class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		Chatter chatter = new ChatService(Chat.inst()).get(event.getPlayer());
		Chat.tasks().sync(() -> {
			// Prevents "t/command"
			if (Pattern.compile("^[tT]" + Commands.getPattern() + ".*").matcher(event.getMessage()).matches())
				runCommand(event.getPlayer(), right(event.getMessage(), event.getMessage().length() - 2));
			else
				chatter.say(event.getMessage());
		});
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onChat(ChatEvent event) {
		Censor.process(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPublicChat(PublicChatEvent event) {
		Koda.process(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDiscordChat(DiscordChatEvent event) {
		Koda.process(event);
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Chatter chatter = new ChatService(Chat.inst()).get(event.getPlayer());
		chatter.updateChannels();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Chatter chatter = new ChatService(Chat.inst()).get(event.getPlayer());
		if (chatter.getActiveChannel() == null)
			chatter.setActiveChannel(ChatManager.getMainChannel());
		chatter.updateChannels();
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		AFKPlayer player = AFK.get(event.getChatter().getPlayer());
		if (player.isAfk())
			player.notAfk();
		else
			player.update();

		if (event.getChannel() instanceof PrivateChannel) {
			for (Chatter recipient : event.getRecipients()) {
				if (!recipient.getOfflinePlayer().isOnline()) continue;
				if (!Utils.canSee(player.getPlayer(), recipient.getPlayer())) return;
				AFKPlayer to = AFK.get(recipient.getPlayer());
				if (AFK.get(to.getPlayer()).isAfk()) {
					Chat.tasks().wait(3, () -> {
						if (!(event.getChatter().getPlayer().isOnline() && to.getPlayer().isOnline())) return;

						String message = "&e* " + to.getPlayer().getName() + " is AFK";
						if (to.getMessage() != null)
							message += ": &3" + to.getMessage();
						event.getChatter().getPlayer().sendMessage(colorize(message));
					});
				}
			}
		}
	}
}
