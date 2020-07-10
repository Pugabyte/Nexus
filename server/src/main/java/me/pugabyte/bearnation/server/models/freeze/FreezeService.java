package me.pugabyte.bearnation.server.models.freeze;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Freeze.class)
public class FreezeService extends MongoService {
	private final static Map<UUID, Freeze> cache = new HashMap<>();

	public FreezeService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, Freeze> getCache() {
		return cache;
	}

}
