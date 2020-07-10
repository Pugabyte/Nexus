package me.pugabyte.bearnation.minigames.features.models.matchdata;

import lombok.Data;
import me.pugabyte.bearnation.minigames.features.mechanics.GrabAJumbuck;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.MatchData;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;
import org.bukkit.entity.Entity;

import java.util.ArrayList;

@Data
@MatchDataFor(GrabAJumbuck.class)
public class GrabAJumbuckMatchData extends MatchData {
	public ArrayList<Entity> sheeps = new ArrayList<>();
	public ArrayList<Entity> items = new ArrayList<>();

	public GrabAJumbuckMatchData(Match match) {
		super(match);
	}
}
