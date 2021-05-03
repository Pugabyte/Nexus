package me.pugabyte.nexus.models.shop;

import eden.mongodb.annotations.PlayerClass;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.models.MongoService;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@PlayerClass(Shop.class)
public class ShopService extends MongoService<Shop> {
	private final static Map<UUID, Shop> cache = new HashMap<>();
	private static final Map<UUID, Integer> saveQueue = new HashMap<>();

	public Map<UUID, Shop> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	static {
		Tasks.async(() -> database.createQuery(Shop.class).find().forEachRemaining(shop -> cache.put(shop.getUuid(), shop)));
	}

	public List<Shop> getShops() {
		return new ArrayList<>(cache.values());
	}

	public List<Shop> getShopsSorted(ShopGroup shopGroup) {
		return getShops().stream()
				.filter(shop -> !shop.isMarket() && !shop.getProducts(shopGroup).isEmpty())
				.sorted(Comparator.comparing(shop -> shop.getInStock(shopGroup).size(), Comparator.reverseOrder()))
				.collect(Collectors.toList());
	}

	public Shop getMarket() {
		return get(Nexus.getUUID0());
	}

	@Override
	protected boolean deleteIf(Shop object) {
		return StringUtils.isUUID0(object.getUuid());
	}

}
