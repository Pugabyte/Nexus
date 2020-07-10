package me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;

public class MinigamerScoredEvent extends MinigamerEvent {
	@Getter
	@Setter
	private int amount;

	public MinigamerScoredEvent(Minigamer minigamer, int amount) {
		super(minigamer);
		this.amount = amount;
	}

}
