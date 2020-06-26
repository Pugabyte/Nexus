package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.snoweffect.SnowEffect;
import me.pugabyte.bncore.models.snoweffect.SnowEffectService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.List;

@Aliases("togglesnow")
public class SnowEffectCommand extends CustomCommand {
	private final SnowEffectService service = new SnowEffectService();
	private SnowEffect snowEffect;

	public SnowEffectCommand(CommandEvent event) {
		super(event);
		snowEffect = service.get(player());
	}

	@Path("[on|off]")
	void lava(Boolean enable) {
		if (enable == null)
			enable = !snowEffect.isEnabled();

		snowEffect.setEnabled(enable);
		service.save(snowEffect);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	static {
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			Tasks.async(() -> {
				List<SnowEffect> all = new SnowEffectService().getAll();
				all.stream()
						.filter(snowEffect -> snowEffect.isOnline() && snowEffect.isEnabled())
						.forEach(snowEffect -> playSnowEffect(snowEffect.getPlayer()));
			});

			Bukkit.getOnlinePlayers().stream()
					.filter(player -> {
						WorldGuardUtils worldGuardUtils = new WorldGuardUtils(player);
						return worldGuardUtils.getRegionsLikeAt(player.getLocation(), ".*_snowEffect").size() > 0;
					})
					.forEach(SnowEffectCommand::playSnowEffect);
		});
	}

	private static void playSnowEffect(Player player) {
		Tasks.sync(() -> {
			if (isBelowCeiling(player))
				return;
			player.spawnParticle(Particle.FALLING_DUST, player.getLocation(), 1400, 40, 15, 40, .01, Bukkit.createBlockData(Material.SNOW_BLOCK));
			Tasks.wait(20, () -> player.spawnParticle(Particle.FIREWORKS_SPARK, player.getLocation(), 1400, 40, 15, 40, .01));
		});
	}

	private static boolean isBelowCeiling(Player player) {
		int count = 0;
		int playerY = (int) player.getLocation().getY() + 1;
		for (int y = playerY; y <= 255; y++) {
			if (player.getLocation().getBlock().getRelative(0, y - playerY, 0).getType().isOccluding())
				++count;
			if (count >= 2)
				return true;
		}
		return false;
	}
}