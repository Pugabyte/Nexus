package me.pugabyte.bearnation.api;

import me.pugabyte.bearnation.api.framework.commands.Commands;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time.Timer;
import me.pugabyte.bearnation.api.utils.WorldGuardFlagUtils;
import org.bukkit.event.Listener;

import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

public class BNCore extends BNAPI {
	private static BNCore staticInstance;

	public BNCore() {
		instance = this;
		staticInstance = this;
	}

	public static BNCore inst() {
		return staticInstance;
	}

	public BNCore getInstance() {
		return inst();
	}

	public static Tasks tasks() {
		return tasks();
	}

	public static void log(String message) {
		inst().getLogger().info(stripColor(message));
	}

	public static void warn(String message) {
		inst().getLogger().warning(stripColor(message));
	}

	public static void severe(String message) {
		inst().getLogger().severe(stripColor(message));
	}

	public static void registerListener(Listener listener) {
		if (inst().isEnabled()) {
			inst().getServer().getPluginManager().registerEvents(listener, inst());
			++listenerCount;
		} else
			log("Could not register listener " + listener.toString() + "!");
	}

	public static void fileLog(String file, String message) {
		write(getPath("logs/" + file + ".log"), "[" + StringUtils.shortDateTimeFormat(LocalDateTime.now()) + "] " + message);
	}

	public static void csvLog(String file, String message) {
		write(getPath("logs/" + file + ".csv"), message);
	}

	public static void write(Path path, String message) {
		tasks().async(() -> {
			try {
				boolean exists = !path.toFile().exists();
				if (exists)
					path.toFile().createNewFile();
				try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
					if (exists)
						writer.append(System.lineSeparator());
					writer.append(message);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	@Override
	public void onLoad() {
		WorldGuardFlagUtils.Flags.register();
	}

	@Override
	public void onEnable() {
		super.onEnable();
		new Timer("Enable", () -> {
			new Timer(" Features", this::enableFeatures);
			new Timer(" Commands", () -> commands = new Commands(this, this.getClass().getPackage().getName()).registerAll());
		});
	}

	// @formatter:off
	@Override
	public void onDisable() {
//		try { Minigames.shutdown();									} catch (Throwable ex) { ex.printStackTrace(); }
//		try { AFK.shutdown();										} catch (Throwable ex) { ex.printStackTrace(); }
//		try { Discord.shutdown();									} catch (Throwable ex) { ex.printStackTrace(); }
//		try { LiteBans.shutdown();									} catch (Throwable ex) { ex.printStackTrace(); }
//		try { TrustFeature.shutdown();								} catch (Throwable ex) { ex.printStackTrace(); }
//		try { broadcastReload();									} catch (Throwable ex) { ex.printStackTrace(); }
//		try { Chat.shutdown();										} catch (Throwable ex) { ex.printStackTrace(); }
//		try { BFQuests.shutdown();									} catch (Throwable ex) { ex.printStackTrace(); }
		super.onDisable();
	}
	// @formatter:on;

/* TODO Listener?
	public void broadcastReload() {
		Rank.getOnlineMods().stream()
				.filter(nerd -> nerd.getOfflinePlayer().isOnline() && nerd.getPlayer() != null)
				.map(Nerd::getPlayer)
				.forEach(player -> {
					GeoIP geoIp = new GeoIPService().get(player);
					String message = " &c&l ! &c&l! &eReloading BNCore &c&l! &c&l!";
					if (geoIp != null && geoIp.getTimezone() != null) {
						String timestamp = StringUtils.shortTimeFormat(LocalDateTime.now(ZoneId.of(geoIp.getTimezone().getId())));
						player.sendMessage(colorize("&7 " + timestamp + message));
					} else
						player.sendMessage(colorize(message));
				});
	}
*/

//	public static Achievements achievements;
//	public static AFK afk;
//	public static Chat chat;
//	public static CustomRecipes recipes;
//	public static DailyRewardsFeature dailyRewards;
//	public static Discord discord;
//	public static Documentation documentation;
//	public static Holidays holidays;
//	public static HomesFeature homes;
//	public static JoinQuit joinQuit;
//	public static Listeners listeners;
//	public static McMMO mcmmo;
//	public static ModelListeners modelListeners;
//	public static Minigames minigames;
//	public static Particles particles;
//	public static Quests quests;
//	public static SafeCracker safeCracker;
//	public static Shops shops;
//	public static Tab tab;
//	public static Tickets tickets;
//	public static TrustFeature trust;
//	public static Votes votes;
//	public static Wiki wiki;

	private void enableFeatures() {
		// Load this first
//		new Timer("  MySQL", NerdService::new);
//		new Timer("  MongoDB", HomeService::new);
//		new Timer("  Discord", () -> discord = new Discord());
//
////		new Timer("  Achievements", () -> achievements = new Achievements());
//		new Timer("  AFK", () -> afk = new AFK());
//		new Timer("  Chat", () -> chat = new Chat());
//		new Timer("  CustomRecipes", () -> recipes = new CustomRecipes());
//		new Timer("  DailyRewards", () -> dailyRewards = new DailyRewardsFeature());
////		new Timer("  Documentation", () -> documentation = new Documentation());
//		new Timer("  Holidays", () -> holidays = new Holidays());
//		new Timer("  Homes", () -> homes = new HomesFeature());
//		new Timer("  JoinQuit", () -> joinQuit = new JoinQuit());
//		new Timer("  Listeners", () -> listeners = new Listeners());
//		new Timer("  McMMO", () -> mcmmo = new McMMO());
//		new Timer("  ModelListeners", () -> modelListeners = new ModelListeners());
//		new Timer("  Minigames", () -> minigames = new Minigames());
//		new Timer("  Particles", () -> particles = new Particles());
//		new Timer("  Quests", () -> quests = new Quests());
//		new Timer("  SafeCracker", () -> safeCracker = new SafeCracker());
//		new Timer("  Shops", () -> shops = new Shops());
//		new Timer("  Tab", () -> tab = new Tab());
//		new Timer("  Tickets", () -> tickets = new Tickets());
//		new Timer("  Trust", () -> trust = new TrustFeature());
//		new Timer("  Votes", () -> votes = new Votes());
//		new Timer("  Wiki", () -> wiki = new Wiki());
	}

}