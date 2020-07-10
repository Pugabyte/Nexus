package me.pugabyte.bearnation.server.models.safecracker;

import me.pugabyte.bearnation.api.BNAPI;
import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import me.pugabyte.bearnation.server.models.safecracker.SafeCrackerEvent.SafeCrackerGame;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SafeCrackerEvent.class)
public class SafeCrackerEventService extends MongoService {

	private final static Map<UUID, SafeCrackerEvent> cache = new HashMap<>();

	public SafeCrackerEventService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, SafeCrackerEvent> getCache() {
		return cache;
	}

	public SafeCrackerEvent get() {
		return super.get(BNAPI.getUUID0());
	}

	public SafeCrackerGame getActiveEvent() {
		for (SafeCrackerGame game : get().getGames().values()) {
			if (game.isActive())
				return game;
		}
		return null;
	}

	public void setActiveGame(SafeCrackerGame game) {
		get().getGames().values().stream().filter(SafeCrackerGame::isActive).forEach(_game -> {
			_game.setActive(false);
		});
		game.setActive(true);
		save(get());
	}

}
