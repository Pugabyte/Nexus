package me.pugabyte.bearnation.server.models.rule;

import me.pugabyte.bearnation.api.framework.persistence.service.MySQLService;
import org.bukkit.plugin.Plugin;

public class RuleService extends MySQLService {

	public RuleService(Plugin plugin) {
		super(plugin);
	}

	@Override
	public HasReadRules get(String uuid) {
		HasReadRules first = database.where("uuid = ?", uuid).first(HasReadRules.class);
		if (first.getUuid() == null)
			first.setUuid(uuid);
		return first;
	}

}
