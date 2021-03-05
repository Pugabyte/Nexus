package me.pugabyte.nexus.models.delivery;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.delivery.DeliveryCommand;
import me.pugabyte.nexus.features.delivery.DeliveryWorldMenu;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@Entity("delivery_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class DeliveryUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private Map<WorldGroup, List<Delivery>> deliveries = new HashMap<>();

	@Getter
	private static final List<WorldGroup> supportedWorldGroups = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.CREATIVE, WorldGroup.SKYBLOCK);

	public void add(WorldGroup worldGroup, Delivery delivery) {
		List<Delivery> deliveries = get(worldGroup);
		deliveries.add(delivery);
		getDeliveries().put(worldGroup, deliveries);
	}

	public List<Delivery> get(WorldGroup worldGroup) {
		return deliveries.getOrDefault(worldGroup, new ArrayList<>());
	}

	public void setupDelivery(ItemStack item) {
		new DeliveryWorldMenu(item).open(getPlayer());
	}

	public void remove(WorldGroup worldGroup, Delivery delivery) {
		List<Delivery> deliveries = get(worldGroup);
		deliveries.remove(delivery);
		getDeliveries().put(worldGroup, deliveries);
	}

	public void sendNotification() {
		send(json(DeliveryCommand.PREFIX + "&3You have an unclaimed delivery, use &c/delivery &3to claim it!")
				.command("/delivery")
				.hover("&eClick to view deliveries"));
	}

	@Data
	@RequiredArgsConstructor
	@NoArgsConstructor
	public static class Delivery {
		@NonNull
		private UUID fromUUID;
		private List<ItemStack> items = new ArrayList<>();
		private String message = null;

		public Delivery(UUID fromUUID, String message, List<ItemStack> items) {
			this.fromUUID = fromUUID;
			this.items = items;
			this.message = message;
		}

		public Delivery(UUID fromUUID, List<ItemStack> items) {
			this.fromUUID = fromUUID;
			this.items = items;
		}

		public Delivery(UUID fromUUID, ItemStack... items) {
			this.fromUUID = fromUUID;
			this.items = Arrays.asList(items);
		}

		public Delivery(String message, UUID fromUUID) {
			this.fromUUID = fromUUID;
			this.message = message;
		}

		public static Delivery serverDelivery(List<ItemStack> items) {
			return new Delivery(Nexus.getUUID0(), items);
		}

		public static Delivery serverDelivery(ItemStack... items) {
			return new Delivery(Nexus.getUUID0(), Arrays.asList(items));
		}

		public static Delivery serverDelivery(String message) {
			return new Delivery(message, Nexus.getUUID0());
		}

		public static Delivery serverDelivery(String message, ItemStack... items) {
			return new Delivery(Nexus.getUUID0(), message, Arrays.asList(items));
		}

		public static Delivery serverDelivery(String message, List<ItemStack> items) {
			return new Delivery(Nexus.getUUID0(), message, items);
		}

		public String getFrom() {
			String from = "Server";
			if (!this.fromUUID.equals(Nexus.getUUID0()))
				from = PlayerUtils.getPlayer(this.fromUUID).getName();
			return from;
		}
	}

}