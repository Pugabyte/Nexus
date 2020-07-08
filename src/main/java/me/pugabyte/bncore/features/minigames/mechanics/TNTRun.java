package me.pugabyte.bncore.features.minigames.mechanics;

import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.ArenaManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.annotations.Regenerating;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBeginEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
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
