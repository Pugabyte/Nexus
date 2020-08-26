package me.pugabyte.bncore.models.aeveonproject;

import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import org.bukkit.OfflinePlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(AeveonProjectUser.class)
public class AeveonProjectService extends MongoService {
	private final static Map<UUID, AeveonProjectUser> cache = new HashMap<>();

	public Map<UUID, AeveonProjectUser> getCache() {
		return cache;
	}

	public boolean hasStarted(OfflinePlayer player) {
		List<AeveonProjectUser> users = getAll();
		for (AeveonProjectUser user : users) {
			if (user.getUuid().equals(player.getUniqueId()))
				return true;
		}
		return false;
	}

}