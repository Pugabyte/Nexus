package me.pugabyte.nexus.models.trophy;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static eden.utils.StringUtils.camelCase;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public enum Trophy {

	BEAR_FAIR_2020_PARTICIPATION(Material.STONE),
	BEAR_FAIR_2020_COMPLETION(Material.STONE),

	HALLOWEEN_2020_PARTICIPATION(Material.STONE),
	HALLOWEEN_2020_COMPLETION(Material.STONE),

	PUGMAS_2020_PARTICIPATION(Material.STONE),
	PUGMAS_2020_COMPLETION(Material.STONE),

	EASTER_2021_PARTICIPATION(Material.STONE),
	EASTER_2021_COMPLETION(Material.STONE),

	PRIDE_2021_PARTICIPATION(Material.STONE),
	PRIDE_2021_COMPLETION(Material.STONE),

	BEAR_FAIR_2021_PARTICIPATION(Material.STONE),
	BEAR_FAIR_2021_COMPLETION(Material.STONE),
	;

	@NonNull
	private final Material material;
	private int customModelData;

	public ItemBuilder getItem() {
		return new ItemBuilder(material).name(camelCase(name()) + " Trophy").customModelData(customModelData).untradeable();
	}

	public String getEvent() {
		return name().split("\\d{4}")[0];
	}

	public static List<String> getEvents() {
		return Arrays.stream(values())
				.map(Trophy::getEvent)
				.distinct()
				.toList();
	}

	public static List<Trophy> getTrophies(String event) {
		return Arrays.stream(values())
				.filter(trophy -> trophy.getEvent().equals(event))
				.toList();
	}

	public static List<Trophy> getEarnedTrophies(TrophyHolder holder, String event) {
		return Arrays.stream(values())
				.filter(trophy -> trophy.getEvent().equals(event))
				.filter(holder::hasEarned)
				.toList();
	}

	public static ItemBuilder getDisplayItem(TrophyHolder holder, String event) {
		List<Trophy> trophies = new ArrayList<>(getTrophies(event));
		Collections.reverse(trophies);
		for (Trophy trophy : trophies)
			if (holder.hasEarned(trophy))
				return trophy.getItem();

		throw new InvalidInputException("Could not determine " + event + " display item for " + holder.getNickname());
	}

}
