package me.pugabyte.bearnation.server.models.purchase;

import me.pugabyte.bearnation.api.framework.persistence.service.MySQLService;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class PurchaseService extends MySQLService {

	public PurchaseService(Plugin plugin) {
		super(plugin);
	}

	public List<Purchase> getRecent(int count) {
		return database.sql(
				"select * " +
				"from purchase " +
				"where price > 0 " +
				"and transactionId IN ( " +
						"select transactionId from ( " +
								"select distinct uuid, transactionId, timestamp " +
								"from purchase " +
								"where price > 0 " +
								"group by uuid " +
						") as data " +
				") order by timestamp " +
				"limit " + count
		).results(Purchase.class);
	}

}
