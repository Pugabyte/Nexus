package me.pugabyte.bearnation.server.models.scoreboard;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(ScoreboardUser.class)
public class ScoreboardService extends MongoService {
	private final static Map<UUID, ScoreboardUser> cache = new HashMap<>();

	public ScoreboardService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, ScoreboardUser> getCache() {
		return cache;
	}

	public void delete(ScoreboardUser user) {
		if (user.getScoreboard() != null)
			user.getScoreboard().delete();
		super.delete(user);
	}

}
