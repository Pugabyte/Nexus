package me.pugabyte.bearnation.minigames.features.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;

public class MatchQuitEvent extends MatchEvent {
	@Getter
	@NonNull
	private Minigamer minigamer;

	public MatchQuitEvent(Match match, Minigamer minigamer) {
		super(match);
		this.minigamer = minigamer;
	}

}
