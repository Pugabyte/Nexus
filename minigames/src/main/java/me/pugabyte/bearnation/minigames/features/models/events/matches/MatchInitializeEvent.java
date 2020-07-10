package me.pugabyte.bearnation.minigames.features.models.events.matches;

import me.pugabyte.bearnation.minigames.features.models.Match;

public class MatchInitializeEvent extends MatchEvent {

	public MatchInitializeEvent(final Match match) {
		super(match);
	}

}
