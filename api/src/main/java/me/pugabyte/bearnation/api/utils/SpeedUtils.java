package me.pugabyte.bearnation.api.utils;

import org.bukkit.entity.Player;

public class SpeedUtils {
	public static void resetSpeed(Player player) {
		resetSpeed(player, true);
		resetSpeed(player, false);
	}

	public static void resetSpeed(Player player, boolean isFly) {
		setSpeed(player, 1, isFly);
	}

	public static void setSpeed(Player player, float speed, boolean isFly) {
		if (isFly)
			player.setFlySpeed(getRealMoveSpeed(speed, isFly));
		else
			player.setWalkSpeed(getRealMoveSpeed(speed, isFly));
	}

	private static float getRealMoveSpeed(final float userSpeed, final boolean isFly) {
		final float defaultSpeed = getDefaultSpeed(isFly);

		if (userSpeed < 1f) {
			return defaultSpeed * userSpeed;
		} else {
			float ratio = ((userSpeed - 1) / 9) * (1 - defaultSpeed);
			return ratio + defaultSpeed;
		}
	}

	private static float getDefaultSpeed(boolean isFly) {
		return isFly ? 0.1f : 0.2f;
	}

}
