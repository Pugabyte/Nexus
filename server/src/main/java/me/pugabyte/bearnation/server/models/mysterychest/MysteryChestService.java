package me.pugabyte.bearnation.server.models.mysterychest;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.server.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(MysteryChestPlayer.class)
public class MysteryChestService extends MongoService {

	public Map<UUID, MysteryChestPlayer> cache = new HashMap<>();

	@Override
	public Map<UUID, MysteryChestPlayer> getCache() {
		return cache;
	}
}
