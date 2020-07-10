package me.pugabyte.bearnation.minigames.features.models.matchdata;

import me.pugabyte.bearnation.minigames.features.mechanics.common.CheckpointMechanic;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;

@MatchDataFor(CheckpointMechanic.class)
public class CheckpointMatchData extends CheckpointData {

	public CheckpointMatchData(Match match) {
		super(match);
	}

}
