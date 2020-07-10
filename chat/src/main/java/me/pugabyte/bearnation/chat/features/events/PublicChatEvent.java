package me.pugabyte.bearnation.chat.features.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.chat.models.chat.PublicChannel;

import java.util.Set;

@Data
@AllArgsConstructor
public class PublicChatEvent extends MinecraftChatEvent {
	private final Chatter chatter;
	private PublicChannel channel;
	private String message;

	private Set<Chatter> recipients;

	public boolean wasSeen() {
		if (channel.getDiscordChannel() != null) return true;
		return recipients.stream().anyMatch(recipient -> chatter.getOfflinePlayer() != recipient.getOfflinePlayer() &&
				Utils.canSee(chatter.getOfflinePlayer(), recipient.getOfflinePlayer()));
	}

	@Override
	public void respond(String response) {
		super.respond(response);
		channel.broadcastDiscord(response);
	}

}
