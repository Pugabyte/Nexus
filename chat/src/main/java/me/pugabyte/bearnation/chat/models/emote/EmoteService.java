package me.pugabyte.bearnation.chat.models.emote;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(EmoteUser.class)
public class EmoteService extends MongoService {
	private final static Map<UUID, EmoteUser> cache = new HashMap<>();

	public EmoteService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, EmoteUser> getCache() {
		return cache;
	}

}
