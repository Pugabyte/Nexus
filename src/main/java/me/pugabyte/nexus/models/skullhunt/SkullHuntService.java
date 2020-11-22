package me.pugabyte.nexus.models.skullhunt;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SkullHunter.class)
public class SkullHuntService extends MongoService {
	private final static Map<UUID, SkullHunter> cache = new HashMap<>();

	public Map<UUID, SkullHunter> getCache() {
		return cache;
	}

}