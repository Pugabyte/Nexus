package me.pugabyte.bearnation.api.framework.persistence.service;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

public abstract class DatabaseService {
	protected Plugin plugin;

	public DatabaseService(Plugin plugin) {
		this.plugin = plugin;
	}

	@NotNull
	public Tasks tasks() {
		return new Tasks(plugin);
	}

	public Class<? extends PlayerOwnedObject> getPlayerClass() {
		PlayerClass annotation = getClass().getAnnotation(PlayerClass.class);
		return annotation == null ? null : annotation.value();
	}

	public <T> T get(UUID uuid) {
		throw new UnsupportedOperationException();
	}

	public <T> T get(Player player) {
		return (T) get(player.getUniqueId());
	}

	public <T> T get(OfflinePlayer player) {
		return (T) get(player.getUniqueId());
	}

	abstract public <T> List<T> getAll();

	public <T> void save(T object) {
		if (Bukkit.getServer().isPrimaryThread())
			tasks().async(() -> saveSync(object));
		else
			saveSync(object);
	}

	abstract public <T> void saveSync(T object);

	public <T> void delete(T object) {
		if (Bukkit.getServer().isPrimaryThread())
			tasks().async(() -> deleteSync(object));
		else
			deleteSync(object);
	}

	abstract public <T> void deleteSync(T object);

	public void deleteAll() {
		if (Bukkit.getServer().isPrimaryThread())
			tasks().async(this::deleteAllSync);
		else
			deleteAllSync();
	}

	abstract public void deleteAllSync();

}
