package gg.projecteden.nexus.models.nerd;

import dev.morphia.query.Query;
import gg.projecteden.mongodb.annotations.PlayerClass;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.MongoService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

@PlayerClass(Nerd.class)
public class NerdService extends MongoService<Nerd> {
	private final static Map<UUID, Nerd> cache = new ConcurrentHashMap<>();
	private static final Map<UUID, Integer> saveQueue = new ConcurrentHashMap<>();

	public Map<UUID, Nerd> getCache() {
		return cache;
	}

	protected Map<UUID, Integer> getSaveQueue() {
		return saveQueue;
	}

	public List<Nerd> find(String partialName) {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("pastNames").containsIgnoreCase(sanitize(partialName)));
		long count = query.count();
		if (count > 50)
			throw new InvalidInputException("Too many name matches for &e" + partialName + " &c(" + count + ")");

		Map<UUID, Integer> hoursMap = new HashMap<>() {{
			HoursService service = new HoursService();
			for (Nerd nerd : query.find().toList())
				put(nerd.getUuid(), service.get(nerd).getTotal());
		}};

		Set<UUID> sorted = Utils.sortByValueReverse(hoursMap).keySet();
		return new ArrayList<>(sorted).stream()
				.map(Nerd::of)
				.collect(toList());
	}

	public List<Nerd> getNerdsWithBirthdays() {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("birthday").notEqual(null));
		return query.find().toList();
	}

	public Nerd getFromAlias(String alias) {
		Query<Nerd> query = database.createQuery(Nerd.class);
		query.and(query.criteria("aliases").hasThisOne(alias));
		return query.find().tryNext();
	}

}
