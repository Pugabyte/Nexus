package me.pugabyte.bncore.framework.persistence;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.mapping.codec.Conversions;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import me.pugabyte.bncore.models.chat.PublicChannel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MongoDBPersistence {
	private static Map<MongoDBDatabase, Datastore> databases = new HashMap<>();

	static {
		BNCore.getInstance().addConfigDefault("databases.mongodb.host", "localhost");
		BNCore.getInstance().addConfigDefault("databases.mongodb.port", 27017);
		BNCore.getInstance().addConfigDefault("databases.mongodb.username", "root");
		BNCore.getInstance().addConfigDefault("databases.mongodb.password", "password");
		BNCore.getInstance().addConfigDefault("databases.mongodb.prefix", "");
	}

	static {
		// TODO Need better place
		Conversions.register(String.class, UUID.class, UUID::fromString);
		Conversions.register(UUID.class, String.class, UUID::toString);
		Conversions.register(PublicChannel.class, String.class, PublicChannel::getName);
		Conversions.register(PrivateChannel.class, String.class, channel -> String.join(",", channel.getRecipientsUuids()));
		Conversions.register(String.class, PublicChannel.class, ChatManager::getChannel);
		Conversions.register(String.class, PrivateChannel.class, string -> new PrivateChannel(Arrays.asList(string.split(","))));
	}

	@SneakyThrows
	private static void openConnection(MongoDBDatabase dbType) {
		DatabaseConfig config = new DatabaseConfig("mongodb");

		MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder().credential(MongoCredential
				.createScramSha1Credential(config.getUsername(), "admin", config.getPassword().toCharArray())).build());
		Datastore datastore = Morphia.createDatastore(mongoClient, config.getPrefix() + dbType.getDatabase());
		datastore.getMapper().mapPackage("me.pugabyte.bncore.models");
		datastore.ensureIndexes();
		databases.put(dbType, datastore);
	}

	public static Datastore getConnection(MongoDBDatabase bndb) {
		try {
			if (databases.get(bndb) == null)
				openConnection(bndb);
			return databases.get(bndb);
		} catch (Exception ex) {
			BNCore.severe("Could not establish connection to the MongoDB \"" + bndb.getDatabase() + "\" database: " + ex.getMessage());
			return null;
		}
	}

	public static void shutdown() {
		databases.values().forEach(datastore -> {
			try {
				datastore.getSession().close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}


}
