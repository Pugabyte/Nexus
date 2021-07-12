package me.pugabyte.nexus.models.ambience;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.ambience.AmbienceConfig.Ambience.AmbienceType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.SoundBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.StringUtils.getShortLocationString;

@Data
@Builder
@Entity("ambience_config")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AmbienceConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private List<Ambience> ambiences = new ArrayList<>();
	private transient Map<Location, Ambience> ambienceMap = new HashMap<>();

	@PostLoad
	void postLoad() {
		for (Ambience ambience : ambiences)
			ambienceMap.put(ambience.getLocation(), ambience);
	}

	public List<Ambience> get(AmbienceType type) {
		return ambiences.stream().filter(ambience -> ambience.getType() == type).toList();
	}

	public Ambience get(Location location) {
		return ambienceMap.get(location.toBlockLocation());
	}

	public void add(Ambience ambience) {
		this.ambiences.add(ambience);
		ambienceMap.put(ambience.getLocation(), ambience);
	}

	public boolean delete(Location location) {
		Ambience ambience = get(location);
		if (ambience == null)
			return false;

		return delete(ambience);
	}

	public boolean delete(Ambience ambience) {
		if (!this.ambiences.contains(ambience))
			return false;

		this.ambiences.remove(ambience);
		ambienceMap.remove(ambience.getLocation());
		return true;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Ambience {
		private Location location;
		private AmbienceType type;

		public boolean validate() {
			if (!type.getType().validate(this)) {
				Nexus.warn("[Ambience] " + camelCase(type) + " at " + getShortLocationString(location) + " invalid, removing");
				config().delete(this);
				return false;
			}

			return true;
		}

		static AmbienceConfig config() {
			return new AmbienceConfigService().get0();
		}

		public void play() {
			type.play(location);
		}

		@Getter
		@AllArgsConstructor
		public enum AmbienceType {
			METAL_WINDCHIMES(AmbienceLocationType.ITEM_FRAME, Material.AMETHYST_SHARD, 1) {
				@Override
				void play(Location location) {
					new SoundBuilder("minecraft:custom.windchimes_metal_" + RandomUtils.randomInt(1, 5))
						.location(location)
						.volume(3)
						.pitch(RandomUtils.randomDouble(0.1, 2.0))
						.play();
				}
			},
			;

			private final AmbienceLocationType type;
			private final Material material;
			private final int customModelData;

			abstract void play(Location location);

			public boolean equals(ItemStack itemStack) {
				if (itemStack.getType() != material)
					return false;
				if (new ItemBuilder(itemStack).customModelData() != customModelData)
					return false;

				return true;
			}

			private enum AmbienceLocationType {
				ITEM_FRAME {
					public boolean validate(Ambience ambience) {
						final Location location = ambience.getLocation();
						for (ItemFrame itemFrame : location.getNearbyEntitiesByType(ItemFrame.class, 1, 1, 1)) {
							if (!itemFrame.getLocation().toBlockLocation().equals(location.toBlockLocation()))
								continue;

							if (isNullOrAir(itemFrame.getItem()))
								continue;

							if (!ambience.getType().equals(itemFrame.getItem()))
								continue;

							return true;
						}

						return false;
					}
				},
				BLOCK {
					public boolean validate(Ambience ambience) {
						return ambience.getLocation().getBlock().getType() == ambience.getType().getMaterial();
					}
				},
				;

				abstract public boolean validate(Ambience ambience);
			}
		}

	}

}
