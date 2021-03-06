package gg.projecteden.nexus.models.afk;

import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.models.MySQLService;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AFKService extends MySQLService {

	public void saveAll() {
		for (Player player : PlayerUtils.getOnlinePlayers()) {
			AFKPlayer afkPlayer = AFK.get(player);
			if (afkPlayer.isAfk())
				database.upsert(afkPlayer);
		}
	}

	public Map<UUID, AFKPlayer> getMap() {
		try {
			List<AFKPlayer> results = database.where("uuid in (" + asList(PlayerUtils.getOnlineUuids()) + ")").results(AFKPlayer.class);
			Tasks.async(() -> database.table("afk").delete());
			Map<UUID, AFKPlayer> players = new HashMap<>();
			for (AFKPlayer afkPlayer : results) {
				OfflinePlayer player = PlayerUtils.getPlayer(afkPlayer.getUuid());
				if (player.isOnline())
					players.put(player.getUniqueId(), afkPlayer);
			}
			return players;
		} catch (Exception ex) {
			ex.printStackTrace();
			return new HashMap<>();
		}
	}

}
