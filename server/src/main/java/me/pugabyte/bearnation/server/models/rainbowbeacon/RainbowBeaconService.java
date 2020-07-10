package me.pugabyte.bearnation.server.models.rainbowbeacon;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(RainbowBeacon.class)
public class RainbowBeaconService extends MongoService {
	private final static Map<UUID, RainbowBeacon> cache = new HashMap<>();

	public RainbowBeaconService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, RainbowBeacon> getCache() {
		return cache;
	}

}
