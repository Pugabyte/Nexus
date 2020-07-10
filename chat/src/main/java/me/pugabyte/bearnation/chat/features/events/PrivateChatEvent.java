package me.pugabyte.bearnation.chat.features.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.bearnation.chat.models.chat.Chatter;
import me.pugabyte.bearnation.chat.models.chat.PrivateChannel;

import java.util.Set;

@Data
@AllArgsConstructor
public class PrivateChatEvent extends MinecraftChatEvent {
	private final Chatter chatter;
	private PrivateChannel channel;
	private String message;

	private Set<Chatter> recipients;

	@Override
	public boolean wasSeen() {
		return true;
	}

}
