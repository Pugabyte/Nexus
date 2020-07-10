package me.pugabyte.bearnation.api.framework.persistence.service;

import dev.morphia.Datastore;
import dev.morphia.query.UpdateException;
import me.pugabyte.bearnation.api.framework.exceptions.BNException;
import me.pugabyte.bearnation.api.framework.persistence.MongoDBDatabase;
import me.pugabyte.bearnation.api.framework.persistence.MongoDBPersistence;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class MongoService extends DatabaseService {
	protected static String _id = "_id";
	protected MongoDBDatabase mongoDatabase;
	protected Datastore database;

	public MongoService(Plugin plugin) {
		this(plugin, MongoDBDatabase.BEARNATION);
	}

	public MongoService(Plugin plugin, MongoDBDatabase mongoDatabase) {
		super(plugin);
		this.mongoDatabase = mongoDatabase;
		this.database = MongoDBPersistence.getConnection(plugin, mongoDatabase);
	}

	public abstract <T> Map<UUID, T> getCache();

	public void clearCache() {
		getCache().clear();
	}

	public <T extends PlayerOwnedObject> void cache(T object) {
		getCache().put(object.getUuid(), object);
	}

	@Override
	@NotNull
	public <T> T get(UUID uuid) {
//		if (isEnableCache())
			return (T) getCache(uuid);
//		else
//			return getNoCache(uuid);
	}

	@NotNull
	protected <T extends PlayerOwnedObject> T getCache(UUID uuid) {
		Validate.notNull(getPlayerClass(), "You must provide a player owned class or override get(UUID)");
		if (getCache().containsKey(uuid) && getCache().get(uuid) == null)
			getCache().remove(uuid);
		getCache().computeIfAbsent(uuid, $ -> getNoCache(uuid));
		return (T) getCache().get(uuid);
	}

	protected <T extends PlayerOwnedObject> T getNoCache(UUID uuid) {
		Object object = database.createQuery(getPlayerClass()).field(_id).equal(uuid).first();
		if (object == null)
			object = createPlayerObject(uuid);
		if (object == null)
			plugin.getLogger().info("New instance of " + getPlayerClass().getSimpleName() + " is null");
		return (T) object;
	}

	protected Object createPlayerObject(UUID uuid) {
		try {
			Constructor<? extends PlayerOwnedObject> constructor = getPlayerClass().getDeclaredConstructor(UUID.class);
			constructor.setAccessible(true);
			return constructor.newInstance(uuid);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException ex) {
			throw new BNException(this.getClass().getSimpleName() + " not implemented correctly");
		}
	}

	@Override
	public <T> List<T> getAll() {
		return (List<T>) database.createQuery(getPlayerClass()).find().toList();
	}

	@Override
	public <T> void saveSync(T object) {
		try {
			database.merge(object);
		} catch (UpdateException doesntExistYet) {
			try {
				database.save(object);
			} catch (Exception ex2) {
				plugin.getLogger().warning("Error saving " + object.getClass().getSimpleName() + ": " + object.toString());
				ex2.printStackTrace();
			}
		} catch (Exception ex3) {
			plugin.getLogger().warning("Error updating " + object.getClass().getSimpleName() + ": " + object.toString());
			ex3.printStackTrace();
		}
	}

	@Override
	public <T> void deleteSync(T object) {
		database.delete(object);
		getCache().remove(((PlayerOwnedObject) object).getUuid());
	}

	@Override
	public void deleteAllSync() {
		database.getCollection(getPlayerClass()).drop();
		clearCache();
	}
}
