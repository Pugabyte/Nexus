package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.shop.ShopService;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.models.skincache.SkinCacheService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Timer;
import gg.projecteden.utils.TimeUtils.Timespan;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@NoArgsConstructor
@Permission("group.admin")
public class SkinCacheCommand extends CustomCommand implements Listener {
	private final SkinCacheService service = new SkinCacheService();

	public SkinCacheCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("getHead [player]")
	void getHead(@Arg("self") SkinCache cache) {
		PlayerUtils.giveItem(player(), cache.getHead());
		send(PREFIX + "Gave head of " + cache.getNickname());
	}

	@Async
	@Path("update [player]")
	void update(@Arg("self") SkinCache cache) {
		cache.update();
		PlayerUtils.giveItem(player(), cache.getHead());
		send(PREFIX + "Updated and gave head of " + cache.getNickname());
	}

	@Async
	@Path("updateAll")
	void updateAll() {
		send(updateAll(new SkinCacheService().getOnline()));
	}

	@Async
	@Path("cacheShopHeads")
	void cacheShopHeads() {
		List<SkinCache> caches = new ShopService().getAll().stream()
			.filter(shop -> !shop.getProducts().isEmpty())
			.map(SkinCache::of)
			.collect(Collectors.toList());

		send(updateAll(caches));
	}

	@Async
	@Path("getLastChange [player]")
	void getLastChange(@Arg("self") SkinCache cache) {
		send(PREFIX + "Skin last changed " + Timespan.of(cache.getLastChanged()).format() + " ago");
	}

	@NotNull
	private static String updateAll(List<SkinCache> online) {
		AtomicInteger updated = new AtomicInteger();

		Timer timer = new Timer("updateAllHeads", () -> {
			for (SkinCache skinCache : online)
				if (skinCache.update())
					updated.incrementAndGet();
		});

		return StringUtils.getPrefix("SkinCache") + "Checked " + online.size() + " skins, found " + updated + " updates, took " + timer.getDuration() + "ms";
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		SkinCache.of(event.getPlayer()).update();
	}

}
