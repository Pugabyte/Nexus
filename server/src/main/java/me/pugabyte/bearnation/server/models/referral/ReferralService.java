package me.pugabyte.bearnation.server.models.referral;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.server.models.MongoService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Referral.class)
public class ReferralService extends MongoService {
	private final static Map<UUID, Referral> cache = new HashMap<>();

	public Map<UUID, Referral> getCache() {
		return cache;
	}

}
