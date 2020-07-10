package me.pugabyte.bearnation.minigames.models.jigsawjam;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(JigsawJammer.class)
public class JigsawJamService extends MongoService {
	private final static Map<UUID, JigsawJammer> cache = new HashMap<>();

	public JigsawJamService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, JigsawJammer> getCache() {
		return cache;
	}

}
