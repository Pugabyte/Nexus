package me.pugabyte.bearnation.minigames.features.models.events.matches;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bearnation.minigames.features.models.Match;

public class MatchTimerTickEvent extends MatchEvent {
	@Getter
	@NonNull
	private int time;

	public MatchTimerTickEvent(final Match match, int time) {
		super(match);
		this.time = time;
	}

}
