package me.pugabyte.bearnation.survival.models.honeypot;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HoneyPotGriefer.class)
public class HoneyPotGrieferService extends MongoService {
	private final static Map<UUID, HoneyPotGriefer> cache = new HashMap<>();

	public Map<UUID, HoneyPotGriefer> getCache() {
		return cache;
	}

}
