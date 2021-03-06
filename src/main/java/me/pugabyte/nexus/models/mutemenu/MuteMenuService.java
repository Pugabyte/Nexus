package me.pugabyte.nexus.models.mutemenu;

import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MuteMenuUser.class)
public class MuteMenuService extends MongoService {
	private final static Map<UUID, MuteMenuUser> cache = new HashMap<>();

	@Override
	public Map<UUID, MuteMenuUser> getCache() {
		return cache;
	}
}
