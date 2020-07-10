package me.pugabyte.bearnation.server.models.socialmedia;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(SocialMediaUser.class)
public class SocialMediaService extends MongoService {
	private final static Map<UUID, SocialMediaUser> cache = new HashMap<>();

	public SocialMediaService(Plugin plugin) {
		super(plugin);
	}

	public Map<UUID, SocialMediaUser> getCache() {
		return cache;
	}

}
