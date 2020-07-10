package me.pugabyte.bearnation.server.models.curiosity;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.server.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Curiosity.class)
public class CuriosityService extends MongoService {
	private final static Map<UUID, Curiosity> cache = new HashMap<>();

	public Map<UUID, Curiosity> getCache() {
		return cache;
	}

}
