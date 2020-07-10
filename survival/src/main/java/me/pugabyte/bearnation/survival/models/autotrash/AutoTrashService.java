package me.pugabyte.bearnation.survival.models.autotrash;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.models.MongoService;

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
