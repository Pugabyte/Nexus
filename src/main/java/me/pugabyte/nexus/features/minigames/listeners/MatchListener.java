package me.pugabyte.nexus.features.minigames.listeners;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.managers.MatchManager;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDamageEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.DecimalFormat;

import static me.pugabyte.nexus.utils.Utils.runCommand;
import static me.pugabyte.nexus.utils.Utils.runCommandAsOp;

public class MatchListener implements Listener {

	public MatchListener() {
		registerWaterDamageTask();
	}

	private void registerWaterDamageTask() {
		// TODO: Move to a task triggered by entering the region
		Tasks.repeat(Time.SECOND, Time.SECOND, () ->
			Minigames.getActiveMinigamers().forEach(minigamer -> {
				if (minigamer.isInMatchRegion("waterdamage") && Utils.isInWater(minigamer.getPlayer()))
					minigamer.getPlayer().damage(1.25);
		}));
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Minigamer minigamer = PlayerManager.get((Player) event.getPlayer());
		if (minigamer.getMatch() == null) return;
		if (event.getInventory().getLocation() == null) return;
		if (minigamer.getMatch().getArena().getMechanic().canOpenInventoryBlocks()) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onMatchStart(MatchStartEvent event) {
		event.getMatch().getPlayers().forEach(this::disableCheats);
	}

	@EventHandler
	public void onMatchJoin(MatchJoinEvent event) {
		disableCheats(event.getMinigamer().getPlayer());
	}

	public void disableCheats(Player player) {
		if (player.hasPermission("voxelsniper.sniper"))
			runCommand(player, "b paint");
		if (player.hasPermission("worldguard.region.bypass.*"))
			runCommand(player, "wgedit off");
		if (Utils.isVanished(player))
			runCommand(player, "vanish off");
		runCommandAsOp(player, "pweather clear");
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.getMatch() == null) return;
		if (minigamer.canTeleport()) return;
		if (event.getTo() != null)
			if (event.getFrom().getWorld() == event.getTo().getWorld())
				if (event.getFrom().distance(event.getTo()) < 2)
					return;

		event.setCancelled(true);
		Nexus.log("Minigamer tried to teleport to " + event.getTo() + " but was denied");
		minigamer.tell("You cannot teleport while in a game!");
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (minigamer.getMatch() == null) return;

		minigamer.quit();
	}

	@EventHandler
	public void onMatchQuit(MatchQuitEvent event) {
		MatchManager.janitor();
	}

	// TODO: Break and place events

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying()) return;

		minigamer.getMatch().getArena().getMechanic().onPlayerInteract(minigamer, event);
	}

	@EventHandler
	public void onInventoryMove(InventoryInteractEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Minigamer minigamer = PlayerManager.get((Player) event.getWhoClicked());
		if (!minigamer.isPlaying()) return;

		event.setCancelled(true);
	}

	// TODO: Prevent damage of hanging entities/armor stands/etc
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getEntity() instanceof Player)) return;

		Minigamer victim = PlayerManager.get((Player) event.getEntity());
		if (!victim.isAlive()) return;
		Minigamer attacker = null;
		Projectile projectile = null;
		if (event.getDamager() instanceof Player) {
			attacker = PlayerManager.get((Player) event.getDamager());
		} else if (event.getDamager() instanceof Projectile) {
			projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player) {
				attacker = PlayerManager.get((Player) projectile.getShooter());
			}
		}

		if (victim.getMatch() == null || victim.getTeam() == null) {
			if (attacker != null && (attacker.getMatch() == null || attacker.getTeam() == null)) {
				if (victim.getMatch() != null && attacker.getMatch() == null) {
					// Normal player damaging someone in a minigame
					event.setCancelled(true);
				}
				if (victim.getMatch() == null && attacker.getMatch() != null) {
					// Minigamer damaging normal player
					event.setCancelled(true);
				}
			}
			// Neither in minigames, ignore
			return;
		}

		if (attacker != null) {
			if (!(victim.isPlaying() && attacker.isPlaying())) return;

			if ((victim.isRespawning() || attacker.isRespawning()) || victim.equals(attacker)) {
				event.setCancelled(true);
				return;
			}

			if (!victim.getMatch().isStarted()) {
				event.setCancelled(true);
				return;
			}
		}

		if (attacker == null || victim.getMatch().equals(attacker.getMatch())) {
			// Same match
			Mechanic mechanic = victim.getMatch().getArena().getMechanic();
			if (attacker != null && victim.getTeam().equals(attacker.getTeam()) && mechanic.isTeamGame()) {
				// Friendly fire
				event.setCancelled(true);
			} else {
				// Damaged by opponent
				double newHealth = victim.getPlayer().getHealth() - event.getFinalDamage();

				if (newHealth > 0) {
					MinigamerDamageEvent damageEvent = new MinigamerDamageEvent(victim, attacker, event);
					if (!damageEvent.callEvent()) {
						event.setCancelled(true);
						return;
					}

					if (attacker != null && event.getDamager() instanceof Arrow)
						attacker.tell("&7" + victim.getName() + " is on &c" + new DecimalFormat("#.0").format(newHealth) + " &7HP");

					mechanic.onDamage(damageEvent);

					if (damageEvent.isCancelled())
						event.setCancelled(true);
					return;
				}

				event.setCancelled(true);

				if (projectile != null)
					projectile.remove();

				if (attacker != null && event.getDamager() instanceof Arrow)
					attacker.getPlayer().playSound(attacker.getPlayer().getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 0.1F);

				MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, attacker, event);
				if (!deathEvent.callEvent()) return;

				if (!victim.getMatch().isEnded())
					mechanic.onDeath(deathEvent);
			}
		} else {
			// Different matches
			event.setCancelled(true);
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event;
			if (!(entityDamageByEntityEvent.getDamager() instanceof FallingBlock)) {
				onDamage(entityDamageByEntityEvent);
				return;
			}
		}

		Minigamer victim;

		if (event.getEntity() instanceof Player) {
			victim = PlayerManager.get((Player) event.getEntity());
		} else {
			return;
		}

		if (!victim.isPlaying() || !victim.isAlive()) return;
		Mechanic mechanic = victim.getMatch().getArena().getMechanic();

		if (victim.isRespawning() || !victim.getMatch().isStarted()) {
			event.setCancelled(true);
			return;
		}

		if (event.getFinalDamage() < victim.getPlayer().getHealth()) {
			MinigamerDamageEvent damageEvent = new MinigamerDamageEvent(victim, event);
			if (!damageEvent.callEvent()) {
				event.setCancelled(true);
				return;
			}

			mechanic.onDamage(damageEvent);

			if (damageEvent.isCancelled())
				event.setCancelled(true);
			return;
		}

		event.setCancelled(true);

		MinigamerDeathEvent deathEvent = new MinigamerDeathEvent(victim, event);
		if (!deathEvent.callEvent()) return;

		if (!victim.getMatch().isEnded())
			mechanic.onDeath(deathEvent);
	}

	@EventHandler
	public void onEnterKillRegion(RegionEnteredEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying()) return;
		if (!minigamer.getMatch().isStarted() || !minigamer.isAlive()) return;
		Mechanic mechanic = minigamer.getMatch().getArena().getMechanic();

		Arena arena = minigamer.getMatch().getArena();
		if (arena.ownsRegion(event.getRegion().getId(), "kill"))
			mechanic.kill(minigamer);
	}

	@EventHandler
	public void onItemPickup(EntityPickupItemEvent event) {
		if (!Minigames.isMinigameWorld(event.getEntity().getWorld())) return;
		// TODO: Entity pickups?
		if (!(event.getEntity() instanceof Player)) return;

		Arena arena = ArenaManager.getFromLocation(event.getItem().getLocation());
		if (arena == null) return;
		Match match = MatchManager.find(arena);
		if (match == null) return;
		Player player = (Player) event.getEntity();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isIn(match))
			event.setCancelled(true);
	}
}