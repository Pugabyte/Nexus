package gg.projecteden.nexus.models.worldban;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.utils.Utils.isNullOrEmpty;

@PlayerClass(WorldBan.class)
public class WorldBanService extends MongoService<WorldBan> {
	private final static Map<UUID, WorldBan> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, WorldBan> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected boolean deleteIf(WorldBan worldBan) {
		return isNullOrEmpty(worldBan.getBans());
	}

}
