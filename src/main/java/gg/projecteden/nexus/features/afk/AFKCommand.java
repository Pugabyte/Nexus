package gg.projecteden.nexus.features.afk;

import gg.projecteden.nexus.features.chat.events.MinecraftChatEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown.Part;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DescriptionExtra;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.afk.AFKPlayer;
import gg.projecteden.nexus.models.afk.AFKSettings;
import gg.projecteden.nexus.models.afk.AFKSettingsService;
import gg.projecteden.nexus.models.chat.Chatter;
import gg.projecteden.nexus.models.chat.PrivateChannel;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NoArgsConstructor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Aliases("away")
@NoArgsConstructor
public class AFKCommand extends CustomCommand implements Listener {
	private final AFKSettingsService service = new AFKSettingsService();

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	@Cooldown(@Part(value = Time.SECOND, x = 5))
	void afk(String autoreply) {
		AFKPlayer player = AFK.get(player());

		if (!isNullOrEmpty(autoreply))
			player.setMessage(autoreply);

		if (player.isAfk())
			if (isNullOrEmpty(autoreply))
				player.notAfk();
			else
				player.forceAfk(player::message);
		else
			player.forceAfk(player::afk);
	}

	@Path("settings mobTargeting [enable]")
	@Description("Disable mobs targeting you while you are AFK")
	@DescriptionExtra("Must be AFK for longer than 4 minutes")
	void mobTargeting(Boolean enable) {
		AFKSettings afkSettings = service.get(player());
		if (enable == null)
			enable = !afkSettings.isMobTargeting();

		afkSettings.setMobTargeting(enable);
		service.save(afkSettings);
		send(PREFIX + "Mobs " + (enable ? "&awill" : "&cwill not") + " &3target you while you are AFK");
	}

	@Path("settings mobSpawning [enable]")
	@Description("Disable mobs spawning near you while you are AFK. Helps with server lag and spawn rates for active players")
	@DescriptionExtra("Must be AFK for longer than 4 minutes")
	void mobSpawning(Boolean enable) {
		error("Temporarily disabled");

		AFKSettings afkSettings = service.get(player());
		if (enable == null)
			enable = !afkSettings.isMobSpawning();

		afkSettings.setMobSpawning(enable);
		service.save(afkSettings);
		send(PREFIX + "Mobs " + (enable ? "&awill" : "&cwill not") + " &3spawn near you while you are AFK");
	}

	@Path("settings broadcasts [enable]")
	void hideBroadcasts(Boolean enable) {
		AFKSettings afkSettings = service.get(player());
		if (enable == null)
			enable = !afkSettings.isBroadcasts();

		afkSettings.setBroadcasts(enable);
		service.save(afkSettings);
		send(PREFIX + "Your own AFK broadcasts are now " + (enable ? "&ashown" : "&chidden") + " &3from other players");
	}

	@EventHandler(ignoreCancelled = true)
	public void onChat(MinecraftChatEvent event) {
		AFKPlayer player = AFK.get(event.getChatter().getOnlinePlayer());
		if (player.isAfk())
			player.notAfk();
		else
			player.update();

		if (event.getChannel() instanceof PrivateChannel) {
			for (Chatter recipient : event.getRecipients()) {
				if (!recipient.getOfflinePlayer().isOnline()) continue;
				if (!PlayerUtils.canSee(player.getPlayer(), recipient.getOnlinePlayer())) return;
				AFKPlayer to = AFK.get(recipient.getOnlinePlayer());
				if (AFK.get(to.getPlayer()).isAfk()) {
					Tasks.wait(3, () -> {
						if (!(event.getChatter().getOnlinePlayer().isOnline() && to.getPlayer().isOnline())) return;

						String message = "&e* " + Nickname.of(to.getPlayer()) + " is AFK";
						if (to.getMessage() != null)
							message += ": &3" + to.getMessage();
						send(event.getChatter(), message);
					});
				}
			}
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		Tasks.wait(3, () -> {
			if (!event.getPlayer().isOnline()) return;

			AFKPlayer player = AFK.get(event.getPlayer());
			if (player.isAfk() && !player.isForceAfk())
				player.notAfk();
			else
				player.update();
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		AFK.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityTarget(final EntityTargetLivingEntityEvent event) {
		if (event.getEntity().getType() == EntityType.EXPERIENCE_ORB)
			return;

		if (event.getTarget() instanceof Player player) {
			if (AFK.get(player).isTimeAfk()) {
				AFKSettings afkSettings = new AFKSettingsService().get(player);
				if (!afkSettings.isMobTargeting())
					event.setCancelled(true);
			}
		}
	}

	/*

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntitySpawn(final CreatureSpawnEvent event) {
		if (event.getSpawnReason() != SpawnReason.NATURAL)
			return;

		Entity entity = event.getEntity();
		if (!isHostile(entity))
			return;

		if (isActivatedEntity(entity))
			return;

		event.setCancelled(true);
	}

	static {
		Tasks.repeat(Time.MINUTE.x(5), Time.MINUTE, () -> {
			for (World world : WorldGroup.SURVIVAL.getWorlds()) {
				if (world.getEnvironment() != Environment.NORMAL)
					continue;

				for (Entity entity : world.getEntities()) {
					if (!isHostile(entity))
						continue;

					if (isActivatedEntity(entity))
						continue;

					entity.remove();
				}
			}
		});
	}

	private static boolean isActivatedEntity(Entity entity) {
		int mobSpawnRange = (getMobSpawnRange(entity.getLocation().getWorld()) + 1) * 16;

		if (!StringUtils.isNullOrEmpty(entity.getCustomName()))
			return true;

		if (entity.getVehicle() != null)
			return true;

		Collection<Player> players = entity.getLocation().getNearbyPlayers(mobSpawnRange, 999, mobSpawnRange);

		for (Player player : players)
			if (AFK.get(player).isNotTimeAfk())
				return true;

		for (Player player : players)
			if (new AFKSettingsService().get(player).isMobSpawning())
				return true;

		return false;
	}

	*/

}
