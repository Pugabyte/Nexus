package me.pugabyte.bearnation.survival.models.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@PlayerClass(Shop.class)
public class ShopService extends MongoService {
	private final static Map<UUID, Shop> cache = new HashMap<>();

	public Map<UUID, Shop> getCache() {
		return cache;
	}

	static {
		Tasks.async(() -> database.createQuery(Shop.class).find().forEachRemaining(shop -> cache.put(shop.getUuid(), shop)));
	}

	public List<Shop> getShops() {
		return new ArrayList<>(cache.values());
	}

	public Shop getMarket() {
		return get(BNCore.getUUID0());
	}

}
