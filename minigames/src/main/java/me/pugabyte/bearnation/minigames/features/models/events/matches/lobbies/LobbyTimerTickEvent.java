package me.pugabyte.bearnation.minigames.features.models.events.matches.lobbies;

import lombok.Getter;
import me.pugabyte.bearnation.minigames.features.models.Lobby;
import me.pugabyte.bearnation.minigames.features.models.Match;

public class LobbyTimerTickEvent extends LobbyEvent {
	@Getter
	private int time;

	public LobbyTimerTickEvent(final Match match, final Lobby lobby, int time) {
		super(match, lobby);
		this.time = time;
	}

}
