package gg.projecteden.nexus.models.eventuser;

import dev.morphia.query.Sort;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(EventUser.class)
public class EventUserService extends MongoService<EventUser> {
	private final static Map<UUID, EventUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, EventUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<EventUser> getTopTokens() {
		return getAllSortedBy(Sort.descending("tokens"));
	}

}
