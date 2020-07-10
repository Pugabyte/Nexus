package me.pugabyte.bearnation.api.framework.persistence;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.annotations.Entity;
import dev.morphia.mapping.MapperOptions;
import lombok.SneakyThrows;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.ColorConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.ItemMetaConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.LocalDateConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.UUIDConverter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;

public class MongoDBPersistence {
	protected static final Morphia morphia = new Morphia();
	private static Map<Plugin, Map<MongoDBDatabase, Datastore>> databases = new HashMap<>();

	@SneakyThrows
	private static void openConnection(Plugin plugin, MongoDBDatabase bndb) {
		DatabaseConfig config = new DatabaseConfig("mongodb");

		// Paper compat
		morphia.getMapper().setOptions(MapperOptions.builder().classLoader(Bukkit.getServer().getClass().getClassLoader()).build());
		new Reflections("me.pugabyte.bncore.models").getTypesAnnotatedWith(Entity.class);

		MongoCredential root = MongoCredential.createScramSha1Credential(config.getUsername(), "admin", config.getPassword().toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(), root, MongoClientOptions.builder().build());
		Datastore datastore = morphia.createDatastore(mongoClient, config.getPrefix() + bndb.getDatabase());
		datastore.ensureIndexes();
		morphia.getMapper().getConverters().addConverter(new ColorConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new ItemMetaConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new ItemStackConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new LocalDateConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new LocalDateTimeConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new LocationConverter(morphia.getMapper()));
		morphia.getMapper().getConverters().addConverter(new UUIDConverter(morphia.getMapper()));
		databases.put(plugin, new HashMap<MongoDBDatabase, Datastore>() {{ put(bndb, datastore); }});
	}

	public static Datastore getConnection(Plugin plugin, MongoDBDatabase bndb) {
		try {
			if (databases.get(plugin).get(bndb) == null)
				openConnection(plugin, bndb);
			return databases.get(plugin).get(bndb);
		} catch (Exception ex) {
			plugin.getLogger().severe("Could not establish connection to the MongoDB \"" + bndb.getDatabase() + "\" database: " + ex.getMessage());
			return null;
		}
	}

	public static void shutdown(Plugin plugin) {
		databases.get(plugin).values().forEach(datastore -> {
			try {
				datastore.getMongo().close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}


}
