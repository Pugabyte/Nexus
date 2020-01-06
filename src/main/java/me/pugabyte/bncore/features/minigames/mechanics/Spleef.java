package me.pugabyte.bncore.features.minigames.mechanics;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.features.minigames.managers.PlayerManager;
import me.pugabyte.bncore.features.minigames.models.Arena;
import me.pugabyte.bncore.features.minigames.models.Match;
import me.pugabyte.bncore.features.minigames.models.Minigamer;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teamless.TeamlessMechanic;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Set;

// TODO:
//  - Add materials to whitelist
//  - Anti-camping
//  - Sounds

public class Spleef extends TeamlessMechanic {

	@Override
	public String getName() {
		return "Spleef";
	}

	@Override
	public String getDescription() {
		return "Spleef other players off the map to win!";
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onInitialize(Match match) {
		super.onInitialize(match);
		resetFloors(match);
	}

	@Override
	public void onEnd(Match match) {
		super.onEnd(match);
		resetFloors(match);
	}

	private void resetFloors(Match match) {
		Minigames.getWorldGuardUtils().getRegionsLike("spleef_" + match.getArena().getName() + "_floor_[0-9]+")
				.forEach(floor -> {
					String file = "spleef/" + floor.getId().replaceFirst("spleef_", "");
					Minigames.getWorldEditUtils().paste(file, floor.getMinimumPoint());
				});
	}

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		Match match = minigamer.getMatch();
		Arena arena = match.getArena();

		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
		if (!arena.canUseBlock(event.getClickedBlock().getType())) return;

		Location location = event.getClickedBlock().getLocation();

		RegionManager regionManager = WGBukkit.getRegionManager(Minigames.getGameworld());
		Set<ProtectedRegion> regions = regionManager.getApplicableRegions(location).getRegions();
		for (ProtectedRegion region : regions) {
			if (isArenaRegion(region.getId(), arena, "floor")) {
				event.setCancelled(true);
				location.getBlock().setType(Material.AIR);
				break;
			}
		}
	}

	@EventHandler
	void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(this)) return;

		Arena arena = minigamer.getMatch().getArena();

		if (isArenaRegion(event.getRegion().getId(), arena, "kill"))
			kill(minigamer);
	}

	@Override
	public void kill(Minigamer minigamer) {
		minigamer.setAlive(false);
		minigamer.toSpectate();
	}

}
