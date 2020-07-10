package me.pugabyte.bearnation.server.models.wallsofgrace;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(WallsOfGrace.class)
public class WallsOfGraceService extends MongoService {
	private final static Map<UUID, WallsOfGrace> cache = new HashMap<>();

	public Map<UUID, WallsOfGrace> getCache() {
		return cache;
	}

}
