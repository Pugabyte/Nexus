package me.pugabyte.bearnation.minigames.features.models.matchdata;

import lombok.Data;
import me.pugabyte.bearnation.minigames.features.mechanics.HoliSplegg;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.MatchData;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;
import org.bukkit.entity.ArmorStand;

@Data
@MatchDataFor(HoliSplegg.class)
public class HoliSpleggMatchData extends MatchData {

	public ArmorStand armorStand;
	public int time = 0;

	public HoliSpleggMatchData(Match match) {
		super(match);
	}

}
