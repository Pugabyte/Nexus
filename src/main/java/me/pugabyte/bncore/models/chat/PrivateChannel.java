package me.pugabyte.bncore.models.chat;

import lombok.Data;
import lombok.ToString;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
public class PrivateChannel implements Channel {
	@ToString.Exclude
	// Not Chatter because serialization stackoverflow
	private Set<UUID> recipients = new HashSet<>();

	public PrivateChannel(List<String> recipients) {
		this.recipients = recipients.stream().map(UUID::fromString).collect(Collectors.toSet());
	}

	public PrivateChannel(Chatter... recipients) {
		this.recipients = Arrays.stream(recipients).map(Chatter::getUuid).collect(Collectors.toSet());
	}

	public PrivateChannel(Set<Chatter> recipients) {
		this.recipients = recipients.stream().map(Chatter::getUuid).collect(Collectors.toSet());
	}

	public Set<Chatter> getRecipients() {
		return getRecipients(null);
	}

	@Override
	public Set<Chatter> getRecipients(Chatter chatter) {
		return recipients.stream().map(uuid -> new ChatService().get(uuid)).collect(Collectors.toSet());
	}

	@ToString.Include
	public Set<String> getRecipientsNames() {
		return recipients.stream()
				.map(uuid -> Utils.getPlayer(uuid).getName())
				.collect(Collectors.toSet());
	}

	@ToString.Include
	public Set<String> getRecipientsUuids() {
		return recipients.stream().map(UUID::toString).collect(Collectors.toSet());
	}

	public Set<Chatter> getOthers(Chatter chatter) {
		return recipients.stream()
				.filter(uuid -> !uuid.equals(chatter.getUuid()))
				.map(uuid -> new ChatService().get(uuid))
				.collect(Collectors.toSet());
	}

	public Set<String> getOthersNames(Chatter chatter) {
		return getOthers(chatter).stream()
				.map(recipient -> recipient.getOfflinePlayer().getName())
				.collect(Collectors.toSet());
	}

	@Override
	public String getAssignMessage(Chatter chatter) {
		return "Now chatting with &f" + String.join(", ", getOthersNames(chatter));
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.YELLOW;
	}

}
