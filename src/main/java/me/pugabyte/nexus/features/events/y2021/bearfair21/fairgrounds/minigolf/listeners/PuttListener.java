package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfColor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfHole;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.inventivetalent.glow.GlowAPI;

import java.util.List;

public class PuttListener implements Listener {

	public PuttListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onPutt(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand()))
			return;

		Player player = event.getPlayer();
		if (!BearFair21.isAtBearFair(event.getPlayer())) {
			return;
		}

		if (isInteracting(event)) {
			return;
		}

		ItemStack item = event.getItem();
		if (ItemUtils.isNullOrAir(item)) {
			return;
		}

		// quick fix
		ItemStack clone = item.clone();
		clone.setAmount(1);
		boolean stop = true;
		for (ItemStack _item : MiniGolf.getItems()) {
			if (ItemUtils.isFuzzyMatch(clone, _item))
				stop = false;
		}
		if (stop) {
			return;
		}
		//

		event.setCancelled(true);

		MiniGolf21User user = MiniGolfUtils.getUser(player.getUniqueId());
		if (!user.isPlaying())
			return;

		// Get info
		World world = player.getWorld();
		Action action = event.getAction();
		Block block = event.getClickedBlock();

		// Get type of golf club
		boolean putter = ItemUtils.isFuzzyMatch(item, MiniGolf.getPutter());
		boolean wedge = ItemUtils.isFuzzyMatch(item, MiniGolf.getWedge());

		if (putter || wedge) {
			// Find entities
			List<Entity> entities = player.getNearbyEntities(5.5, 5.5, 5.5);

			Location eye = player.getEyeLocation();
			Vector dir = eye.getDirection();
			Vector loc = eye.toVector();

			if (user.getSnowball() == null)
				return;

			for (Entity entity : entities) {

				// Are we allowed to hit this ball?
				if (entity instanceof Snowball) {
					Snowball ball = (Snowball) entity;

					// Check this again, cuz NPE for some reason
					if (user.getSnowball() == null) {
						MiniGolfUtils.error(user, "Ball is null (2)");
						return;
					}

					if (!user.getSnowball().equals(ball))
						continue;

					// Is golf ball in player's view?
					Location entityLoc = ball.getLocation();
					Vector vec = entityLoc.toVector().subtract(loc);

					if (dir.angle(vec) < 0.15f) {

						// Are we hitting or picking up the golf ball?
						if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
							// Hit golf ball
							dir.setY(0).normalize();

							double power = player.getExp();
							if (power >= 0.90)
								power = 1.0;
							else if (power < 0.16)
								power = 0.16;

							dir.multiply(power);
							if (wedge)
								dir.setY(0.25);

							// Update stroke
							user.incStrokes();
							ball.setCustomName(MiniGolfUtils.getStrokeString(user));

							// Update last pos
							if (MiniGolfUtils.isInBounds(user, entityLoc))
								user.setBallLocation(entityLoc.add(0, MiniGolf.getFloorOffset(), 0));

							ball.setTicksLived(1);
							ball.setVelocity(dir);
							world.playSound(entityLoc, Sound.BLOCK_METAL_HIT, 0.75f, 1.25f);

							ActionBarUtils.sendActionBar(player, "&6Power: " + getPowerDisplay(power), Time.SECOND.x(3));
							user.setSnowball(ball);
						} else if (ball.isValid()) {
							// Give golf ball
							user.removeBall();
							MiniGolfUtils.giveBall(user);
						}
					}
				}
			}
		} else if (ItemUtils.isFuzzyMatch(item, MiniGolf.getGolfBall().build())) {
			// Is player placing golf ball?
			if (action == Action.RIGHT_CLICK_BLOCK) {
				// Has already placed a ball
				if (user.getSnowball() != null) {
					MiniGolfUtils.error(user, "You already have a ball placed");
					return;
				}

				// Is placing on start position
				if (BlockUtils.isNullOrAir(block) || block.getType() != Material.GREEN_WOOL) {
					MiniGolfUtils.error(user, "You can only place golf balls on green wool");
					return;
				}

				// Is on a valid hole
				MiniGolfHole hole = MiniGolfUtils.getHole(block.getLocation());
				if (hole == null) {
					MiniGolfUtils.error(user, "That is not a valid hole");
					return;
				}

				user.setCurrentHole(hole);
				user.setCurrentStrokes(0);

				// Get spawn location
				Location loc = block.getLocation().add(0.5, 1 + MiniGolf.getFloorOffset(), 0.5);
				user.setBallLocation(loc);

				// Spawn golf ball and set data
				Snowball ball = (Snowball) world.spawnEntity(loc, EntityType.SNOWBALL);
				ball.setItem(MiniGolf.getGolfBall().clone().customModelData(user.getMiniGolfColor().getCustomModelData()).build());

				ball.setGravity(false);
				ball.setCustomName(MiniGolfUtils.getStrokeString(user));
				ball.setCustomNameVisible(true);

				user.setSnowball(ball);
				if (!user.getMiniGolfColor().equals(MiniGolfColor.RAINBOW))
					GlowAPI.setGlowing(user.getSnowball(), user.getGlowColor(), user.getPlayer());

				// Remove golf ball from inventory
				ItemStack itemInHand = event.getItem();
				itemInHand.setAmount(itemInHand.getAmount() - 1);
			}
		} else if (ItemUtils.isFuzzyMatch(item, MiniGolf.getWhistle())) {
			// Return ball
			if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
				// Get last player ball
				Snowball ball = user.getSnowball();
				if (ball == null || !ball.isValid()) {
					MiniGolfUtils.sendActionBar(user, "&cYou don't have an active golf ball");
					return;
				}

				// Move ball to last location
				ball.setVelocity(new Vector(0, 0, 0));
				ball.setGravity(false);
				ball.teleport(user.getBallLocation().add(0, MiniGolf.getFloorOffset(), 0));

				// Sound
				world.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.9f, 1.9f);
			}
		} else if (ItemUtils.isFuzzyMatch(item, MiniGolf.getScoreBook())) {
			PlayerUtils.runCommand(player, "minigolf score 1");
		}

	}

	private String getPowerDisplay(double power) {
		int result = (int) (power * 100);

		String color = "&a";
		if (result >= 70)
			color = "&c";
		else if (result >= 50)
			color = "&e";

		return color + result;
	}

	// TODO: better way?
	public boolean isInteracting(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getPlayer().isSneaking())
			return false;

		Block block = event.getClickedBlock();
		return MaterialTag.INTERACTABLES.isTagged(block != null ? block.getType() : null);
	}

}