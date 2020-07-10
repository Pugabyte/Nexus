package me.pugabyte.bearnation.api.models.tip;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Tip.class)
public class TipService extends MongoService {
	private final static Map<UUID, Tip> cache = new HashMap<>();

	public TipService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, Tip> getCache() {
		return cache;
	}

}
