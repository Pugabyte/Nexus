package gg.projecteden.nexus.features.chat.events;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PublicChannel;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class PublicChatEvent extends MinecraftChatEvent {
	private final Chatter chatter;
	private PublicChannel channel;
	private final String originalMessage;
	private String message;
	private boolean filtered;
	private boolean bad;

	public PublicChatEvent(Chatter chatter, PublicChannel channel, String originalMessage, String message, Set<Chatter> recipients) {
		this.chatter = chatter;
		this.channel = channel;
		this.originalMessage = originalMessage;
		this.message = message;
		this.recipients = recipients;
	}

	private Set<Chatter> recipients;

	public boolean wasSeen() {
		if (channel.getDiscordTextChannel() != null) return true;
		return recipients.stream().anyMatch(recipient -> chatter.getOfflinePlayer() != recipient.getOfflinePlayer() &&
				PlayerUtils.canSee(chatter.getOfflinePlayer(), recipient.getOfflinePlayer()));
	}

	@Override
	public void respond(String response) {
		super.respond(response);
		Broadcast.discord().channel(channel).message(response).send();
	}

}
