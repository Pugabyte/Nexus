package me.pugabyte.bearnation.minigames.features.mechanics;

import me.pugabyte.bearnation.minigames.features.mechanics.common.CaptureTheFlagMechanic;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.mechanics.Mechanic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;

public final class OneFlagCaptureTheFlag extends CaptureTheFlagMechanic {

	@Override
	public String getName() {
		return "One Flag Capture the Flag";
	}

	@Override
	public String getDescription() {
		return "Find the flag and capture it at the other team's base";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.BLUE_BANNER);
	}

	@Override
	public void onFlagInteract(Minigamer minigamer, Sign sign) {
		Match match = minigamer.getMatch();
		Arena arena = match.getArena();
		Mechanic mechanic = arena.getMechanic();

		if (!minigamer.isPlaying(this)) return;

		if ((ChatColor.GRAY + "Neutral").equalsIgnoreCase(sign.getLine(2))) {
			// TODO: Taking neutral flag
		} else if ((ChatColor.GREEN + "Capture").equalsIgnoreCase(sign.getLine(2))) {
			// TODO: Capturing neutral flag
		}
	}

}
