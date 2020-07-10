package me.pugabyte.bearnation.server.models.particle;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(ParticleOwner.class)
public class ParticleService extends MongoService {
	public static final Map<UUID, ParticleOwner> cache = new HashMap<>();

	public ParticleService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, ParticleOwner> getCache() {
		return cache;
	}

	@Override
	public <T> void saveSync(T object) {
		database.delete(object);
		database.save(object);
	}

}
