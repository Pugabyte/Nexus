package me.pugabyte.nexus.models.autotrash;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AutoTrash.class)
public class AutoTrashService extends MongoService {
	private final static Map<UUID, AutoTrash> cache = new HashMap<>();

	public Map<UUID, AutoTrash> getCache() {
		return cache;
	}

}