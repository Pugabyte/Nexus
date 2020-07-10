package me.pugabyte.bearnation.minigames.features.models.matchdata;

import lombok.Data;
import me.pugabyte.bearnation.minigames.features.mechanics.MonsterMaze;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.MatchData;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

@Data
@MatchDataFor(MonsterMaze.class)
public class MonsterMazeMatchData extends MatchData {
	private List<LivingEntity> monsters = new ArrayList<>();

	public MonsterMazeMatchData(Match match) {
		super(match);
	}
}
