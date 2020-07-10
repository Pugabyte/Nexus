package me.pugabyte.bearnation.minigames.features.mechanics;

import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.managers.ArenaManager;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.annotations.Regenerating;
import me.pugabyte.bearnation.minigames.features.models.events.matches.MatchBeginEvent;
import me.pugabyte.bearnation.minigames.features.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

@Regenerating("floor")
public class TNTRun extends TeamlessMechanic {

	@Override
	public String getName() {
		return "TNTRun";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.TNT);
	}

	@Override
	public void begin(MatchBeginEvent event) {
		super.begin(event);
		event.getMatch().broadcast("&eGo!");
		new TNTRunTask(event.getMatch());
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if (!event.getLocation().getWorld().equals(Minigames.getWorld()))
			return;

		Arena arena = ArenaManager.getFromLocation(event.getLocation());
		if (arena == null || arena.getMechanic().equals(this))
			return;

		event.blockList().clear();
	}

	public static class TNTRunTask {
		private final Match match;
		private int taskId;

		TNTRunTask(Match match) {
			this.match = match;
			start();
		}

		void start() {
			taskId = match.getTasks().repeat(0, 1, () -> {
				if (match.isEnded())
					stop();

				match.getMinigamers().forEach(minigamer -> {
					Block standingOn = Utils.getBlockStandingOn(minigamer.getPlayer());
					if (standingOn == null)
						return;

					Block tnt = standingOn.getRelative(0, -1, 0);
					if (!tnt.getType().equals(Material.TNT))
						return;

					match.getTasks().wait(4, () -> {
						if (match.isEnded())
							return;
						standingOn.setType(Material.AIR);
						tnt.setType(Material.AIR);
					});
				});

			});
		}

		void stop() {
			Tasks.cancel(taskId);
		}
	}
}
