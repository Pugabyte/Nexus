package me.pugabyte.bearnation.server.models.hallofhistory;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(HallOfHistory.class)
public class HallOfHistoryService extends MongoService {
	public static Map<UUID, HallOfHistory> cache = new HashMap<>();

	public HallOfHistoryService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, HallOfHistory> getCache() {
		return cache;
	}

	public void save(HallOfHistory hallOfHistory) {
		if (hallOfHistory.getRankHistory() == null || hallOfHistory.getRankHistory().size() == 0)
			super.delete(hallOfHistory);
		else
			super.save(hallOfHistory);
	}

}
