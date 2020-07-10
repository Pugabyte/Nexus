package me.pugabyte.bearnation.server.models.snoweffect;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SnowEffect.class)
public class SnowEffectService extends MongoService {
	private final static Map<UUID, SnowEffect> cache = new HashMap<>();

	public SnowEffectService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, SnowEffect> getCache() {
		return cache;
	}

}
