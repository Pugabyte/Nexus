package me.pugabyte.bearnation.server.features.achievements.listeners;

import me.pugabyte.bearnation.api.utils.JsonBuilder;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.features.achievements.events.AchievementCompletedEvent;
import me.pugabyte.bearnation.server.models.achievement.Achievement;
import me.pugabyte.bearnation.server.models.achievement.AchievementGroup;
import me.pugabyte.bearnation.server.models.achievement.AchievementPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AchievementListener implements Listener {

	@EventHandler
	public void onAchievementCompleted(AchievementCompletedEvent event) {
		AchievementPlayer achievementPlayer = event.getAchievementPlayer();
		Achievement achievement = event.getAchievement();

		achievementPlayer.addAchievement(achievement);
		achievementPlayer.setAchievementProgress(achievement, null);

		// Spammy...
		if (achievement.getGroup() == AchievementGroup.BIOMES) return;

		BNPlugin.log(achievementPlayer.getPlayer().getName() + " has completed the " + achievement.toString() + " achievement");

		Player player = achievementPlayer.getPlayer();
		if (player.isOnline()) {
			String message = StringUtils.getPrefix("Achievements") + "You have completed the &e" + achievement.toString() + " &3achievement!";
			player.sendMessage(new JsonBuilder(message).hover("&e" + achievement.getDescription()).build());
		}

	}

}
