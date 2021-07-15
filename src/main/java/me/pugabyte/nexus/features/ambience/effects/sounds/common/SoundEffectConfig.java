package me.pugabyte.nexus.features.ambience.effects.sounds.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import me.pugabyte.nexus.models.ambience.AmbienceUser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

@Data
@AllArgsConstructor
public class SoundEffectConfig {
	private static final Random RANDOM = new Random();
	//
	private SoundEffectType effectType;
	private List<Sound> sounds;
	private int cooldownMin;
	private int cooldownMax;
	private String cooldownId;

	public SoundEffectConfig(SoundEffectType effectType, List<Sound> sounds, int cooldownMin, int cooldownMax) {
		this.effectType = effectType;
		this.sounds = sounds;
		this.cooldownMin = cooldownMin;
		this.cooldownMax = cooldownMax;
		this.cooldownId = effectType.name().toLowerCase();

		if (cooldownMin < 0) throw new IllegalArgumentException("cooldown minimum cannot be negative");
		if (cooldownMax < 0) throw new IllegalArgumentException("cooldown maximum cannot be negative");
		if (cooldownMax < cooldownMin)
			throw new IllegalArgumentException("cooldown min cannot be larger than cooldown max");
	}

	public void init(AmbienceUser user) {
		setCooldown(user);
	}

	public void update(AmbienceUser user) {
		Player player = user.getPlayer();
		if (player == null)
			return;

		if (!effectType.conditionsMet(this, user))
			return;

		if (user.updateCooldown(cooldownId) <= 0) {
			for (Sound sound : sounds)
				user.getSoundPlayer().playSound(sound, player.getLocation());

			setCooldown(user);
		}
	}

	private void setCooldown(AmbienceUser user) {
		user.setCooldown(cooldownId, cooldownMin + RANDOM.nextInt(cooldownMax - cooldownMin + 1));
	}

}
