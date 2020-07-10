package me.pugabyte.bearnation.minigames.features.models.events.matches.teams;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Team;

public class TeamScoredEvent extends TeamEvent {
	@Getter
	@Setter
	private int amount;

	public TeamScoredEvent(Match match, Team team, int amount) {
		super(match, team);
		this.amount = amount;
	}

}
