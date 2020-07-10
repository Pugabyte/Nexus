package me.pugabyte.bearnation.minigames.features.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.events.MinigameEvent;

public class MatchEvent extends MinigameEvent {
	@Getter
	@NonNull
	private Match match;

	public MatchEvent(Match match) {
		super(match.getArena());
		this.match = match;
	}

}
