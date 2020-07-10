package me.pugabyte.bearnation.server;

import me.pugabyte.bearnation.api.BNAPI;
import me.pugabyte.bearnation.api.utils.Tasks;
import org.bukkit.event.Listener;

import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

public class Server extends BNAPI {
	private static Server staticInstance;

	public Server() {
		instance = this;
		staticInstance = this;
	}

	public static Server inst() {
		return staticInstance;
	}

	public Server getInstance() {
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
}
