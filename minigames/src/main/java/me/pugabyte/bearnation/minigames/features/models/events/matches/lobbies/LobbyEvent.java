package me.pugabyte.bearnation.minigames.features.models.events.matches.lobbies;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bearnation.minigames.features.models.Lobby;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.events.matches.MatchEvent;

public class LobbyEvent extends MatchEvent {
	@NonNull
	@Getter
	private Lobby lobby;

	public LobbyEvent(Match match, Lobby lobby) {
		super(match);
		this.lobby = lobby;
	}

}
