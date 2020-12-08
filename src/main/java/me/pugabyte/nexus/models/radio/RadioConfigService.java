package me.pugabyte.nexus.models.radio;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RadioConfig.class)
public class RadioConfigService extends MongoService {
	private final static Map<UUID, RadioConfig> cache = new HashMap<>();

	public Map<UUID, RadioConfig> getCache() {
		return cache;
	}

}