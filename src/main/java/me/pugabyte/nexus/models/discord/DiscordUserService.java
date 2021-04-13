package me.pugabyte.nexus.models.discord;

import me.pugabyte.nexus.features.discord.DiscordId.Role;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.annotations.PlayerClass;
import me.pugabyte.nexus.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(DiscordUser.class)
public class DiscordUserService extends MongoService {
	private final static Map<UUID, DiscordUser> cache = new HashMap<>();

	public Map<UUID, DiscordUser> getCache() {
		return cache;
	}

	public DiscordUser checkVerified(String userId) {
		DiscordUser user = getFromUserId(userId);

		if (user == null || user.getUserId() == null || user.getUuid() == null || !user.getMember().getRoles().contains(Role.VERIFIED.get()))
			throw new InvalidInputException("You must link your Discord and Minecraft accounts before using this command");

		return user;
	}

	public DiscordUser getFromUserId(String userId) {
		DiscordUser user = database.createQuery(DiscordUser.class).filter("userId", userId).find().tryNext();
		cache(user);
		return user;
	}

	public DiscordUser getFromRoleId(String roleId) {
		DiscordUser user = database.createQuery(DiscordUser.class).filter("roleId", roleId).find().tryNext();
		cache(user);
		return user;
	}

}
