package me.pugabyte.bearnation.survival.models.dumpster;

import me.pugabyte.bearnation.BNCore;
import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Dumpster.class)
public class DumpsterService extends MongoService {
	private final static Map<UUID, Dumpster> cache = new HashMap<>();

	public Map<UUID, Dumpster> getCache() {
		return cache;
	}

	public Dumpster get() {
		return super.get(BNCore.getUUID0());
	}

}
