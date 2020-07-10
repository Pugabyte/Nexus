package me.pugabyte.bearnation.server.models.compass;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.server.models.PlayerOwnedObject;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.UUID;

@Data
@Builder
@Entity("compass")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Compass extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	private transient int taskId;
	private transient BossBar bossBar;

	public void start() {
		if (!enabled)
			return;

		if (!getOfflinePlayer().isOnline())
			return;

		stop();

		bossBar = Bukkit.createBossBar(getCompass(), BarColor.BLUE, BarStyle.SEGMENTED_6);
		bossBar.addPlayer(getPlayer());
		taskId = Tasks.repeat(0, 1, () -> {
			if (!getOfflinePlayer().isOnline())
				stop();
			else if (bossBar != null)
				bossBar.setTitle(getCompass());
		});
	}

	public void stop() {
		Tasks.cancel(taskId);
		if (bossBar != null) {
			bossBar.setVisible(false);
			bossBar.removeAll();
			bossBar = null;
		}
	}

	public String getCompass() {
		return StringUtils.compass(getPlayer(), 14, 6);
	}
}
