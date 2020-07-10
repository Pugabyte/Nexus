package me.pugabyte.bearnation.chat.features.alerts;

import lombok.NoArgsConstructor;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.events.DiscordChatEvent;
import me.pugabyte.bearnation.chat.features.events.MinecraftChatEvent;
import me.pugabyte.bearnation.chat.models.alerts.AlertsService;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.chat.models.chat.PrivateChannel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class AlertsListener implements Listener {

	@NotNull
	public Set<Chatter> getEveryoneElse(Chatter origin, Set<Chatter> recipients) {
		return recipients.stream().filter(chatter -> !chatter.equals(origin)).collect(Collectors.toSet());
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		Set<Chatter> everyoneElse = getEveryoneElse(event.getChatter(), event.getRecipients());
		if (event.getChannel() instanceof PrivateChannel) {
			everyoneElse.forEach(Chatter::playSound);
		} else
			tryAlerts(everyoneElse, event.getMessage());
	}

	@EventHandler(ignoreCancelled = true)
	public void onDiscordMessage(DiscordChatEvent event) {
		Set<Chatter> everyoneElse = event.getRecipients();
		if (event.getChatter() != null)
			everyoneElse = getEveryoneElse(event.getChatter(), event.getRecipients());
		tryAlerts(everyoneElse, event.getMessage());
	}

	public void tryAlerts(Set<Chatter> recipients, String message) {
		List<String> uuids = recipients.stream().map(chatter -> chatter.getUuid().toString()).collect(Collectors.toList());
		new AlertsService(Chat.inst()).getAll(uuids).forEach(alerts -> alerts.tryAlerts(message));
	}

}
