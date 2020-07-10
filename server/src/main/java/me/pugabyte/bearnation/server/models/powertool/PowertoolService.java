package me.pugabyte.bearnation.server.models.powertool;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(PowertoolUser.class)
public class PowertoolService extends MongoService {
	private final static Map<UUID, PowertoolUser> cache = new HashMap<>();

	public PowertoolService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, PowertoolUser> getCache() {
		return cache;
	}

}
