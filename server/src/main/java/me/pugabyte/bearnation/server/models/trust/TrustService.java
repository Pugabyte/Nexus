package me.pugabyte.bearnation.server.models.trust;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.server.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Trust.class)
public class TrustService extends MongoService {
	private final static Map<UUID, Trust> cache = new HashMap<>();

	public Map<UUID, Trust> getCache() {
		return cache;
	}

}
