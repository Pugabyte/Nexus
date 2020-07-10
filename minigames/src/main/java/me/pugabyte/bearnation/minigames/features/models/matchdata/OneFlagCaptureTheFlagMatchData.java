package me.pugabyte.bearnation.minigames.features.models.matchdata;

import lombok.Data;
import me.pugabyte.bearnation.minigames.features.mechanics.OneFlagCaptureTheFlag;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.MatchData;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;
import org.bukkit.Location;

@Data
@MatchDataFor(OneFlagCaptureTheFlag.class)
public class OneFlagCaptureTheFlagMatchData extends MatchData {
	Location originalFlagLocation;
	Minigamer flagCarrier;

	public OneFlagCaptureTheFlagMatchData(Match match) {
		super(match);
	}

}
