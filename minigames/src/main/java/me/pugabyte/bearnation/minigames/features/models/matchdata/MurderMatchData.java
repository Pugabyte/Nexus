package me.pugabyte.bearnation.minigames.features.models.matchdata;

import lombok.Data;
import me.pugabyte.bearnation.minigames.features.mechanics.Murder;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.MatchData;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;

@Data
@MatchDataFor(Murder.class)
public class MurderMatchData extends MatchData {

	public MurderMatchData(Match match) {
		super(match);
	}
}
