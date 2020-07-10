package me.pugabyte.bearnation.api.framework.persistence;

import com.dieselpoint.norm.Database;
import com.dieselpoint.norm.sqlmakers.MySqlMaker;
import lombok.SneakyThrows;
import me.pugabyte.bearnation.api.BNCore;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MySQLPersistence {
	private static Map<Plugin, Map<MySQLDatabase, Database>> databases = new HashMap<>();

	@SneakyThrows
	private static void openConnection(Plugin plugin, MySQLDatabase bndb) {
		Class.forName("com.mysql.jdbc.Driver");

		DatabaseConfig config = new DatabaseConfig("mysql");
		Database database = new Database();
		database.setJdbcUrl("jdbc:mysql://" + config.getHost() + ":" + config.getPort() + "/" + config.getPrefix() + bndb.getDatabase() + "?useSSL=false&relaxAutoCommit=true&characterEncoding=UTF-8");
		database.setUser(config.getUsername());
		database.setPassword(config.getPassword());
		database.setSqlMaker(new MySqlMaker());
		database.setMaxPoolSize(3);
		databases.put(plugin, new HashMap<MySQLDatabase, Database>() {{ put(bndb, database); }});
	}

	public static Database getConnection(Plugin plugin, MySQLDatabase dbType) {
		try {
			if (databases.get(plugin).get(dbType) == null)
				openConnection(plugin, dbType);
			return databases.get(plugin).get(dbType);
		} catch (Exception ex) {
			BNCore.severe("Could not establish connection to the MySQL \"" + dbType.getDatabase() + "\" database: " + ex.getMessage());
			return null;
		}
	}

	public static void shutdown(Plugin plugin) {
		databases.get(plugin).values().forEach(database -> {
			try {
				database.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

}
