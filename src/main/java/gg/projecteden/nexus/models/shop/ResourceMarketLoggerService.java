package gg.projecteden.nexus.models.shop;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ResourceMarketLogger.class)
public class ResourceMarketLoggerService extends MongoService<ResourceMarketLogger> {
	private final static Map<UUID, ResourceMarketLogger> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, ResourceMarketLogger> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

}
