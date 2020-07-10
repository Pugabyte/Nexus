package me.pugabyte.bearnation.server.models.compass;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.server.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Compass.class)
public class CompassService extends MongoService {
	private final static Map<UUID, Compass> cache = new HashMap<>();

	public Map<UUID, Compass> getCache() {
		return cache;
	}

}
