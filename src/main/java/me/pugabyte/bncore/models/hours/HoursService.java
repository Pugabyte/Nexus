package me.pugabyte.bncore.models.hours;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bncore.models.MongoService;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.models.hoursold.HoursOld;
import me.pugabyte.bncore.models.hoursold.HoursOldService;
import me.pugabyte.bncore.models.nerd.Nerd;
import me.pugabyte.bncore.models.nerd.NerdService;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.skip;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@PlayerClass(Hours.class)
public class HoursService extends MongoService {
	private final static Map<UUID, Hours> cache = new HashMap<>();

	public Map<UUID, Hours> getCache() {
		return cache;
	}

	private static final MongoCollection<Document> collection = database.getDatabase().getCollection("hours");

	@Override
	@Deprecated // Use HoursService#update to increment daily counter
	public <T> void save(T object) {
		super.save(object);
	}

	public void update(Hours hours) {
		database.update(
				database.createQuery(Hours.class).field(_id).equal(hours.getUuid()),
				database.createUpdateOperations(Hours.class).set("times." + DateTimeFormatter.ISO_DATE.format(LocalDate.now()), hours.getDaily())
		);
	}

	public void migrate() {
		int count = 0;
		for (HoursOld hoursOld : new HoursOldService().getAll()) {
			OfflinePlayer player = hoursOld.getPlayer();
			Nerd nerd = new NerdService().get(player);
			Hours hours = new Hours(player.getUniqueId());

			if (nerd.getLastQuit() == null || nerd.getFirstJoin().isAfter(nerd.getLastQuit())) continue;

			LocalDate start = nerd.getFirstJoin().toLocalDate().withDayOfMonth(1);
			LocalDate end = nerd.getLastQuit().toLocalDate().withDayOfMonth(1);

			int months = (int) ChronoUnit.MONTHS.between(start, end);
			int spread = (hoursOld.getTotal() / Math.max(months, 1)) / 5 * 5;

			if (start.equals(end))
				hours.getTimes().put(start, spread);
			else
				while (start.isBefore(end)) {
					hours.getTimes().put(start, spread);
					start = start.plusMonths(1);
				}

			cache.put(player.getUniqueId(), hours);
			save(hours);

			++count;
			if (count % 100 == 0)
				BNCore.log("Migrated " + count + " records");
		}
	}

//	public int total(HoursType type) {
//		return database.select("sum(" + type.columnName() + ")").table("hours").first(Double.class).intValue();
//	}

	/*

		db.hours.aggregate([
			{ $project : { _id : "$_id", times : { $objectToArray: "$times" } } },
			{ $project : { _id : "$_id", total : { $sum : "$times.v" } } },
			{ $sort : { 'total': -1 } }
		]);

	 */

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class PageResult extends PlayerOwnedObject {
		@Id
		@NonNull
		private UUID uuid;
		private int total;
	}

	public List<PageResult> getPage(int page) {
		List<Bson> arguments = getTopArguments();
		arguments.add(skip((page - 1) * 10));
		arguments.add(limit(10));

		return getPageResults(collection.aggregate(arguments));

//		return database.createAggregation(Hours2.class)
//				.project(Projection.projection(_id, "_id"), Projection.projection("times", Projection.projection("$objectToArray", "$times")))
//				.project(Projection.projection(_id, "_id"), Projection.projection("total", Projection.projection("$sum", "$times.v")))
//				.sort(Sort.descending("total"))
//				.aggregate(PageResult.class);
	}

	@NotNull
	public List<PageResult> getPageResults(AggregateIterable<Document> aggregate) {
		return new ArrayList<PageResult>() {{
			aggregate.forEach((Consumer<? super Document>) document ->
					add(new PageResult(UUID.fromString((String) document.get("_id")), (int) document.get("total"))));
		}};
	}

	@NotNull
	public List<Bson> getTopArguments() {
		return new ArrayList<>(Arrays.asList(
				project(fields(
						include("_id"),
						computed("times", new BasicDBObject("$objectToArray", "$times"))
				)),
//				project(match(regex("times.k", "2020-05-.*"))),
				project(fields(
						include("_id"),
						computed("total", new BasicDBObject("$sum", "$times.v"))
				)),
				sort(Sorts.descending("total"))
		));
	}

	// TODO
	public List<Hours> getActivePlayers() {
		List<Bson> arguments = getTopArguments();
		arguments.add(limit(100));

		return getPageResults(collection.aggregate(arguments)).stream()
				.map(pageResult -> (Hours) get(pageResult.getUuid()))
				.collect(Collectors.toList());
	}

	public HoursType getType(String type) {
		if (type == null || type.contains("total")) return HoursType.TOTAL;

		if (type.contains("month"))
			if (type.contains("last"))
				return HoursType.LAST_MONTH;
			else
				return HoursType.MONTHLY;

		if (type.contains("week"))
			if (type.contains("last"))
				return HoursType.LAST_WEEK;
			else
				return HoursType.WEEKLY;

		if (type.contains("day") || type.contains("daily"))
			if (type.contains("yester") || type.contains("last"))
				return HoursType.YESTERDAY;
			else
				return HoursType.DAILY;

		throw new InvalidInputException("Invalid leaderboard type. Options are: " + HoursType.valuesString());
	}

	public enum HoursType {
		TOTAL,
		MONTHLY,
		WEEKLY,
		DAILY,
		LAST_MONTH,
		LAST_WEEK,
		YESTERDAY;

		public String columnName() {
			return camelCase(name()).replaceAll(" ", "");
		}

		public static String valuesString() {
			return Arrays.stream(values())
					.map(Enum::name)
					.collect(Collectors.joining(","))
					.toLowerCase();
		}
	}

}
