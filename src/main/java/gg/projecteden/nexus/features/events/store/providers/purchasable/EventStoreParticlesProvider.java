package gg.projecteden.nexus.features.events.store.providers.purchasable;

import fr.minuskube.inv.ClickableItem;
import gg.projecteden.nexus.features.events.store.EventStoreItem;
import gg.projecteden.nexus.features.events.store.providers.EventStoreMenu;
import gg.projecteden.nexus.models.particle.ParticleOwner;
import gg.projecteden.nexus.models.particle.ParticleService;
import gg.projecteden.nexus.models.particle.ParticleType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.EnumUtils;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.features.events.Events.STORE_PREFIX;

@AllArgsConstructor
public class EventStoreParticlesProvider extends EventStoreMenu {
	@Getter
	private final EventStoreMenu previousMenu;

	@Override
	protected String getTitle() {
		return "Event Store - Particles";
	}

	@NotNull
	@Override
	protected List<ClickableItem> getItems(Player player) {
		List<ClickableItem> items = new ArrayList<>();

		ParticleService service = new ParticleService();
		ParticleOwner particleOwner = service.get(player);
		int price = EventStoreItem.PARTICLES.getPrice();

		for (ParticleType type : EnumUtils.valuesExcept(ParticleType.class, ParticleType.WINGS)) {
			if (particleOwner.canUse(type))
				continue;

			ItemBuilder item = type.getDisplayItem();
			lore(player, item, price);

			items.add(ClickableItem.from(item.build(), e -> {
				try {
					if (isShiftClick(e))
						chargeAndAddPermissions(player, price, type.getPermission());
					else {
						player.closeInventory();
						type.run(player);
						Tasks.wait(Time.SECOND.x(15), () -> particleOwner.cancel(type));
					}
				} catch (Exception ex) {
					handleException(player, STORE_PREFIX, ex);
				}
			}));
		}
		return items;
	}

}
