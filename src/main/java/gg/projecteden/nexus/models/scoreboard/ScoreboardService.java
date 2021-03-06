package gg.projecteden.nexus.models.scoreboard;

import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.models.MongoService;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@PlayerClass(ScoreboardUser.class)
public class ScoreboardService extends MongoService<ScoreboardUser> {
	private final static Map<UUID, ScoreboardUser> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, ScoreboardUser> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	@Override
	protected void beforeDelete(ScoreboardUser user) {
		if (user.getScoreboard() != null)
			user.getScoreboard().delete();
	}

}
