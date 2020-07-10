package me.pugabyte.bearnation.server.models.assetcompetition;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.server.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AssetCompetition.class)
public class AssetCompetitionService extends MongoService {
	private final static Map<UUID, AssetCompetition> cache = new HashMap<>();

	public Map<UUID, AssetCompetition> getCache() {
		return cache;
	}

}
