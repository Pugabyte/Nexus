package me.pugabyte.bearnation.server.models.poof;

import com.dieselpoint.norm.Query;
import me.pugabyte.bearnation.api.framework.persistence.service.MySQLService;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

public class PoofService extends MySQLService {

	public PoofService(Plugin plugin) {
		super(plugin);
	}

	public Poof getBySender(Player sender) {
		Poof first = database.where("sender = ? and expired = 0", sender.getUniqueId().toString()).first(Poof.class);
		if (first.getSender() == null)
			first = null;
		return first;
	}

	public Poof getByReceiver(Player receiver) {
		Poof first = database.where("receiver = ? and expired = 0", receiver.getUniqueId().toString()).first(Poof.class);
		if (first.getSender() == null)
			first = null;
		return first;
	}

	public List<Poof> getActivePoofs() {
		return getActivePoofs(null);
	}

	public List<Poof> getActivePoofs(UUID uuid) {
		Query query = database.where("expired = 0");
		if (uuid != null)
			query.and("sender = ?", uuid);
		return query.results(Poof.class);
	}

}
