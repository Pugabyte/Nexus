package gg.projecteden.nexus.features.listeners;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.WorldGuardFlagUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.utils.TimeUtils.Time;
import joptsimple.internal.Strings;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.MoistureChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.commands.staff.WorldGuardEditCommand.canWorldGuardEdit;
import static gg.projecteden.nexus.utils.EntityUtils.isHostile;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.ACTIONBAR_TICKS;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.ALLOW_SPAWN;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.FAREWELL_ACTIONBAR;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.FAREWELL_SUBTITLE;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.FAREWELL_TITLE;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.GRASS_DECAY;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.GREETING_ACTIONBAR;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.GREETING_SUBTITLE;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.GREETING_TITLE;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.HANGING_BREAK;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.HOSTILE_SPAWN;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.MINIGAMES_WATER_DAMAGE;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.MOB_AGGRESSION;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.TAMING;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.TITLE_FADE;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.TITLE_TICKS;
import static gg.projecteden.nexus.utils.WorldGuardFlagUtils.Flags.USE_TRAP_DOORS;

public class WorldGuardFlags implements Listener {

	@EventHandler
	public void onItemFrameBreak(HangingBreakEvent event) {
		if (RemoveCause.ENTITY.equals(event.getCause()))
			return;

		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onBlockSpread(BlockSpreadEvent event) {
		if (event.getNewState().getType() == Material.BAMBOO)
			if (WorldGuardFlagUtils.query(event.getBlock().getLocation(), Flags.CROP_GROWTH) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onBonemealUse(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;
		if (!Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) return;

		ItemStack item = event.getItem();
		if (ItemUtils.isNullOrAir(item)) return;
		if (!item.getType().equals(Material.BONE_MEAL)) return;

		Block clicked = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(clicked)) return;
		if (!(clicked instanceof Ageable ageable)) return;

		int age = ageable.getAge();
		if (age == ageable.getMaximumAge()) return;

		if (canWorldGuardEdit(event.getPlayer())) return;
		if (WorldGuardFlagUtils.query(clicked.getLocation(), Flags.CROP_GROWTH) != State.DENY) return;

		ageable.setAge(++age);
		clicked.setBlockData(ageable);
	}

	@EventHandler
	public void onCreatureSpawnAllow(CreatureSpawnEvent event) {
		try {
			Set<com.sk89q.worldedit.world.entity.EntityType> entityTypeSet = WorldGuardFlagUtils.queryValue(event.getLocation(), ALLOW_SPAWN);
			List<EntityType> entityTypeList = new ArrayList<>();
			if (entityTypeSet == null) return;
			entityTypeSet.forEach(entityType -> {
				try {
					entityTypeList.add(EntityType.valueOf(entityType.getName().toUpperCase().replace("MINECRAFT:", "")));
				} catch (Exception ignore) {}
			});
			if (entityTypeList.isEmpty()) return;
			if (!entityTypeList.contains(event.getEntityType()))
				event.setCancelled(true);
		} catch (Exception ignore) {}
	}

	@EventHandler
	public void onItemFrameBreak(HangingBreakByEntityEvent event) {
		Entity remover = event.getRemover();
		if (remover instanceof Player)
			return;

		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), HANGING_BREAK) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler
	public void onGrassDecay(BlockFadeEvent event) {
		if (event.getBlock().getType() == Material.GRASS_BLOCK && event.getNewState().getType() == Material.DIRT)
			if (WorldGuardFlagUtils.query(event.getBlock().getLocation(), GRASS_DECAY) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (isHostile(event.getEntity()))
			if (WorldGuardFlagUtils.query(event.getLocation(), HOSTILE_SPAWN) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() != null)
			if (WorldGuardFlagUtils.query(event.getTarget().getLocation(), MOB_AGGRESSION) == State.DENY)
				event.setCancelled(true);
	}

	@EventHandler
	public void onEntityTame(EntityTameEvent event) {
		if (WorldGuardFlagUtils.query(event.getEntity().getLocation(), TAMING) == State.DENY) {
			event.setCancelled(true);
			PlayerUtils.send(event.getOwner(), "&c&lHey! &7Sorry, but you can't tame that here.");
		}
	}

	static {
		Tasks.repeat(Time.SECOND, Time.SECOND, () ->
				Minigames.getActiveMinigamers().forEach(minigamer -> {
					if (minigamer.getPlayer().isInWater())
						if (WorldGuardFlagUtils.query(minigamer.getPlayer().getLocation(), MINIGAMES_WATER_DAMAGE) == State.ALLOW)
							minigamer.getPlayer().damage(1.25);
				}));
	}

	@EventHandler
	public void onInteractTrapDoor(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block) || !(MaterialTag.TRAPDOORS.isTagged(block.getType())))
			return;

		if (WorldGuardFlagUtils.query(block, USE_TRAP_DOORS) == State.DENY) {
			if (canWorldGuardEdit(event.getPlayer()))
				return;
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onSoilMoistureChange(MoistureChangeEvent event) {
		Block block = event.getBlock();
		if (!block.getType().equals(Material.FARMLAND))
			return;

		Farmland from = (Farmland) block.getBlockData();
		Farmland to = (Farmland) event.getNewState().getBlockData();
		if (from.getMoisture() <= to.getMoisture())
			return;

		if (WorldGuardFlagUtils.query(block.getLocation(), com.sk89q.worldguard.protection.flags.Flags.SOIL_DRY) == State.DENY)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();

		// Action Bar
		String greeting_actionbar = (String) event.getRegion().getFlag(GREETING_ACTIONBAR.get());
		if (!Strings.isNullOrEmpty(greeting_actionbar)) {
			Integer actionbar_ticks = (Integer) event.getRegion().getFlag(ACTIONBAR_TICKS.get());
			if (actionbar_ticks == null)
				actionbar_ticks = 60;
			else if (actionbar_ticks < 1)
				actionbar_ticks = 1;

			ActionBarUtils.sendActionBar(player, greeting_actionbar, actionbar_ticks);
		}

		// Titles
		String greeting_title = (String) event.getRegion().getFlag(GREETING_TITLE.get());
		String greeting_subtitle = (String) event.getRegion().getFlag(GREETING_SUBTITLE.get());
		if (!(Strings.isNullOrEmpty(greeting_title) && Strings.isNullOrEmpty(greeting_subtitle))) {
			if (greeting_title == null)
				greeting_title = "";
			if (greeting_subtitle == null)
				greeting_subtitle = "";

			Integer title_ticks = (Integer) event.getRegion().getFlag(TITLE_TICKS.get());
			if (title_ticks == null)
				title_ticks = 200;
			else if (title_ticks < 1)
				title_ticks = 1;

			Integer title_fade = (Integer) event.getRegion().getFlag(TITLE_FADE.get());
			if (title_fade == null)
				title_fade = 20;
			else if (title_fade < 1)
				title_fade = 1;

			new TitleBuilder().players(player).title(greeting_title).subtitle(greeting_subtitle).fade(title_fade).stay(title_ticks).send();
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onExitRegion(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();

		World world = WorldGuardUtils.getWorld(event.getRegion());
		if (world != null && !world.equals(player.getWorld()))
			return;

		String farewell_actionbar = (String) event.getRegion().getFlag(FAREWELL_ACTIONBAR.get());
		if (!Strings.isNullOrEmpty(farewell_actionbar)) {

			Integer actionbar_ticks = (Integer) event.getRegion().getFlag(ACTIONBAR_TICKS.get());
			if (actionbar_ticks == null)
				actionbar_ticks = 60;
			else if (actionbar_ticks < 1)
				actionbar_ticks = 1;

			ActionBarUtils.sendActionBar(player, farewell_actionbar, actionbar_ticks);
		}

		// Titles
		String farewell_title = (String) event.getRegion().getFlag(FAREWELL_TITLE.get());
		String farewell_subtitle = (String) event.getRegion().getFlag(FAREWELL_SUBTITLE.get());
		if (!(Strings.isNullOrEmpty(farewell_title) && Strings.isNullOrEmpty(farewell_subtitle))) {
			if (Strings.isNullOrEmpty(farewell_title))
				farewell_title = "";
			if (Strings.isNullOrEmpty(farewell_subtitle))
				farewell_subtitle = "";

			Integer title_ticks = (Integer) event.getRegion().getFlag(TITLE_TICKS.get());
			if (title_ticks == null)
				title_ticks = 200;
			else if (title_ticks < 1)
				title_ticks = 1;

			Integer title_fade = (Integer) event.getRegion().getFlag(TITLE_FADE.get());
			if (title_fade == null)
				title_fade = 20;
			else if (title_fade < 1)
				title_fade = 1;

			new TitleBuilder().players(player).title(farewell_title).subtitle(farewell_subtitle).fade(title_fade).stay(title_ticks).send();
		}
	}

}
