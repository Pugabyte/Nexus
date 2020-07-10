package me.pugabyte.bearnation.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.earth2me.essentials.Essentials;
import it.sauronsoftware.cron4j.Scheduler;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import me.pugabyte.bearnation.api.framework.commands.Commands;
import me.pugabyte.bearnation.api.framework.persistence.MongoDBPersistence;
import me.pugabyte.bearnation.api.framework.persistence.MySQLPersistence;
import me.pugabyte.bearnation.api.utils.SignMenuFactory;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.api.utils.Utils.EnumUtils;
import net.buycraft.plugin.bukkit.BuycraftPluginBase;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public abstract class BNAPI extends JavaPlugin {
	protected BNAPI instance;
	@Getter
	protected Tasks tasks;
	@Getter
	@Accessors(fluent = true)
	protected Commands commands;
	@Getter
	protected final static UUID UUID0 = new UUID(0, 0);
	@Getter
	protected static int listenerCount = 0;

	@Getter
	private Scheduler cron = new Scheduler(); // http://www.sauronsoftware.it/projects/cron4j/manual.php
	@Getter
	private SignMenuFactory signMenuFactory;
	@Getter
	private ProtocolManager protocollib;
	@Getter
	private Essentials essentials;
	@Getter
	private BuycraftPluginBase buycraft;
	@Getter
	private Economy econ = null;
	@Getter
	private Permission perms = null;
	@Getter
	private LuckPerms luckPerms = null;

	public BNAPI() {
		this.tasks = new Tasks(instance);
	}

	protected abstract BNAPI getInstance();

	@SneakyThrows
	public static Path getPath(String path) {
		return Paths.get("plugins/BNCore/" + path);
	}

	@SneakyThrows
	public static File getFile(String path) {
		File file = getPath(path).toFile();
		if (!file.exists()) file.createNewFile();
		return file;
	}

	public static YamlConfiguration config() {
		return YamlConfiguration.loadConfiguration(configFile());
	}

	public static File configFile() {
		return getFile("config.yml");
	}

	@SneakyThrows
	public static void addConfigDefault(String path, Object value) {
		FileConfiguration config = config();
		config.addDefault(path, value);

		config.options().copyDefaults(true);
		config.save(configFile());
	}

	@SneakyThrows
	public static YamlConfiguration getConfig(String path) {
		return YamlConfiguration.loadConfiguration(getFile(path));
	}

	public enum Env {
		DEV,
		PROD
	}

	public static Env getEnv() {
		String env = config().getString("env", Env.DEV.name()).toUpperCase();
		try {
			return Env.valueOf(env);
		} catch (IllegalArgumentException ex) {
			BNCore.severe("Could not parse environment variable " + env + ", options are: " + String.join(", ", EnumUtils.valueNameList(Env.class)));
			BNCore.severe("Defaulting to " + Env.DEV.name() + " environment");
			return Env.DEV;
		}
	}

	@Override
	public void onEnable() {
		setupConfig();
		signMenuFactory = new SignMenuFactory(this);
		protocollib = ProtocolLibrary.getProtocolManager();
		essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		buycraft = (BuycraftPluginBase) Bukkit.getServer().getPluginManager().getPlugin("BuycraftX");
		econ = getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		perms = getServer().getServicesManager().getRegistration(Permission.class).getProvider();
		luckPerms = getServer().getServicesManager().getRegistration(LuckPerms.class).getProvider();
		cron.start();
	}

	@Override
	public void onDisable() {
		try { Utils.runCommandAsConsole("save-all");			} catch (Throwable ex) { ex.printStackTrace(); }
		try { cron.stop();										} catch (Throwable ex) { ex.printStackTrace(); }
		try { commands.unregisterAll();							} catch (Throwable ex) { ex.printStackTrace(); }
		try { protocollib.removePacketListeners(instance);		} catch (Throwable ex) { ex.printStackTrace(); }
		try { MySQLPersistence.shutdown(instance);				} catch (Throwable ex) { ex.printStackTrace(); }
		try { MongoDBPersistence.shutdown(instance);			} catch (Throwable ex) { ex.printStackTrace(); }
	}

	@SneakyThrows
	private void setupConfig() {
		if (!instance.getDataFolder().exists())
			instance.getDataFolder().mkdir();

		FileConfiguration config = config();

		addConfigDefault("env", "dev");

		config.options().copyDefaults(true);
		config.save(configFile());
	}

}
