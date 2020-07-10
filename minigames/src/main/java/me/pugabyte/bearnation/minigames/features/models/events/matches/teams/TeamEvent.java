package me.pugabyte.bearnation.minigames.features.models.events.matches.teams;

import lombok.Getter;
import lombok.Setter;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Team;
import me.pugabyte.bearnation.minigames.features.models.events.matches.MatchEvent;

public class TeamEvent extends MatchEvent {
	@Getter
	@Setter
	private Team team;

	public TeamEvent(Match match, Team team) {
		super(match);
		this.team = team;
	}

}
