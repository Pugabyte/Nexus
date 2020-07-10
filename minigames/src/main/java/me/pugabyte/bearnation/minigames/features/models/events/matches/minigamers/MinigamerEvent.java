package me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.events.matches.MatchEvent;

public class MinigamerEvent extends MatchEvent {
	@Getter
	@NonNull
	Minigamer minigamer;

	public MinigamerEvent(Minigamer minigamer) {
		super(minigamer.getMatch());
		this.minigamer = minigamer;
	}

}
