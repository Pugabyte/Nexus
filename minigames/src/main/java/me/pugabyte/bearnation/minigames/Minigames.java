package me.pugabyte.bearnation.minigames;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.lucko.helper.Services;
import me.lucko.helper.scoreboard.PacketScoreboard;
import me.lucko.helper.scoreboard.PacketScoreboardProvider;
import me.pugabyte.bearnation.api.BNAPI;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.api.utils.WorldEditUtils;
import me.pugabyte.bearnation.api.utils.WorldGroup;
import me.pugabyte.bearnation.api.utils.WorldGuardUtils;
import me.pugabyte.bearnation.minigames.features.lobby.ActionBar;
import me.pugabyte.bearnation.minigames.features.lobby.Basketball;
import me.pugabyte.bearnation.minigames.features.managers.ArenaManager;
import me.pugabyte.bearnation.minigames.features.managers.MatchManager;
import me.pugabyte.bearnation.minigames.features.managers.PlayerManager;
import me.pugabyte.bearnation.minigames.features.menus.MinigamesMenus;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.bearnation.api.utils.StringUtils.colorize;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

public class Minigames extends BNAPI {
	public static final String PREFIX = StringUtils.getPrefix("Minigames");
	@Getter
	private static final World world = Bukkit.getWorld("gameworld");
	@Getter
	private static final Location lobby = new Location(world, 1861.5, 38.1, 247.5, 0, 0);
	@Getter
	@Deprecated // Use Match#getWGUtils or Arena#getWGUtils
	private static final WorldGuardUtils worldGuardUtils = new WorldGuardUtils(world);
	@Getter
	@Deprecated // Use Match#getWEUtils or Arena#getWEUtils
	private static final WorldEditUtils worldEditUtils = new WorldEditUtils(world);
	@Getter
	private static final ProtectedRegion lobbyRegion = worldGuardUtils.getProtectedRegion("minigamelobby");
	@Getter
	public static final MinigamesMenus menus = new MinigamesMenus();
	@Getter
	public static final PacketScoreboard scoreboard = Services.load(PacketScoreboardProvider.class).getScoreboard();

	private static Minigames staticInstance;

	public Minigames() {
		instance = this;
		staticInstance = this;
	}

	public static Minigames inst() {
		return staticInstance;
	}

	public Minigames getInstance() {
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

	@Override
	public void onEnable() {
		super.onEnable();
		registerSerializables();
		ArenaManager.read();
		registerListeners();
		tasks().repeat(Time.SECOND.x(5), 10, MatchManager::janitor);

		new ActionBar();
		new Basketball();
	}

	@Override
	public void onDisable() {
		new ArrayList<>(MatchManager.getAll()).forEach(Match::end);
		ArenaManager.write();
		super.onDisable();
	}

	public static boolean isMinigameWorld(World world) {
		return WorldGroup.get(world) == WorldGroup.MINIGAMES;
	}

	public static List<Player> getPlayers() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> isMinigameWorld(player.getWorld())).collect(Collectors.toList());
	}

	public static List<Minigamer> getMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).collect(Collectors.toList());
	}

	public static List<Minigamer> getActiveMinigamers() {
		return getPlayers().stream().map(PlayerManager::get).filter(minigamer -> minigamer.getMatch() != null).collect(Collectors.toList());
	}

	public static void broadcast(String announcement) {
		getPlayers().forEach(player -> player.sendMessage(Minigames.PREFIX + colorize(announcement)));

		// TODO: If arena is public, announce to discord and whole server
	}

	// Registration

	private String getPath() {
		return this.getClass().getPackage().getName();
	}

	private void registerListeners() {
		for (Class<? extends Listener> clazz : new Reflections(getPath() + ".listeners").getSubTypesOf(Listener.class)) {
			try {
				registerListener(clazz.newInstance());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void registerSerializables() {
		new Reflections(getPath()).getTypesAnnotatedWith(SerializableAs.class).forEach(clazz -> {
			String alias = clazz.getAnnotation(SerializableAs.class).value();
			ConfigurationSerialization.registerClass((Class<? extends ConfigurationSerializable>) clazz, alias);
		});
	}

}
