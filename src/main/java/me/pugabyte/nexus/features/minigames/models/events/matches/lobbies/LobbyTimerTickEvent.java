package me.pugabyte.nexus.features.minigames.models.events.matches.lobbies;

import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.Lobby;
import me.pugabyte.nexus.features.minigames.models.Match;

public class LobbyTimerTickEvent extends LobbyEvent {
	@Getter
	private int time;

	public LobbyTimerTickEvent(final Match match, final Lobby lobby, int time) {
		super(match, lobby);
		this.time = time;
	}

}