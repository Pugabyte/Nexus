package me.pugabyte.bearnation.minigames.features.models.matchdata;

import lombok.Data;
import me.pugabyte.bearnation.minigames.features.mechanics.UncivilEngineers;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.annotations.MatchDataFor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@MatchDataFor(UncivilEngineers.class)
public class UncivilEngineersMatchData extends CheckpointData {
	public List<Entity> entities = new ArrayList<>();
	public Map<UUID, Integer> playerStrips = new HashMap<>();
	public Map<UUID, List<EntityType>> playerEntities = new HashMap<>();

	public UncivilEngineersMatchData(Match match) {
		super(match);
	}
}
