package me.pugabyte.nexus.features.listeners;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTTileEntity;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Koda;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.warps.Warps;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ItemUtils.getTool;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

public class Misc implements Listener {

	@EventHandler
	public void onCoralDeath(BlockFadeEvent event) {
		Block block = event.getBlock();
		if (MaterialTag.ALL_CORALS.isTagged(block.getType())) {
			WorldGroup worldGroup = WorldGroup.get(block.getWorld());
			if (WorldGroup.CREATIVE.equals(worldGroup) || WorldGroup.ADVENTURE.equals(worldGroup) || WorldGroup.MINIGAMES.equals(worldGroup))
				event.setCancelled(true);
		}
	}

	@EventHandler
	public void onHorseLikeDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof AbstractHorse)
			if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION))
				event.setCancelled(true);
	}

	@EventHandler
	public void onWanderingTraderSpawn(EntitySpawnEvent event) {
		List<EntityType> types = Arrays.asList(EntityType.WANDERING_TRADER, EntityType.TRADER_LLAMA);
		List<WorldGroup> worlds = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.SKYBLOCK);
		if (types.contains(event.getEntity().getType()))
			if (!worlds.contains(WorldGroup.get(event.getLocation().getWorld())))
				event.setCancelled(true);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled()) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();

		if (event.getCause() == DamageCause.VOID)
			if (!WorldGroup.get(player).equals(WorldGroup.SKYBLOCK) && (!player.getWorld().getName().contains("the_end"))) {
				if (PlayerManager.get(player).getMatch() != null)
					Warps.spawn((Player) event.getEntity());
			}
	}

	@EventHandler
	public void onPlayerDamageByPlayer(PlayerDamageByPlayerEvent event) {
		if (event.getVictim().getUniqueId().equals(event.getAttacker().getUniqueId()))
			event.getOriginalEvent().setCancelled(true);
	}

	@EventHandler
	public void onPlaceChest(BlockPlaceEvent event) {
		if (WorldGroup.get(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!event.getBlockPlaced().getType().equals(Material.CHEST))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.LWC_FURNACE))
			Utils.send(event.getPlayer(), Koda.getDmFormat() + "Your chest is protected with LWC! Use /lwcinfo to learn more. " +
					"Use &c/trust lock <player> &eto allow someone else to use it.");
	}

	@EventHandler
	public void onPlaceFurnace(BlockPlaceEvent event) {
		if (WorldGroup.get(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!event.getBlockPlaced().getType().equals(Material.FURNACE))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.LWC_FURNACE))
			Utils.send(event.getPlayer(), Koda.getDmFormat() + "Your furnace is protected with LWC! Use /lwcinfo to learn more. " +
					"Use &c/trust lock <player> &eto allow someone else to use it.");
	}

	@EventHandler
	public void onJoinWithChatDisabled(PlayerJoinEvent event) {
		Tasks.wait(Time.SECOND.x(3), () -> {
			Player player = event.getPlayer();
			ChatVisibility setting = player.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (Arrays.asList(ChatVisibility.SYSTEM, ChatVisibility.HIDDEN).contains(setting)) {
				ActionBarUtils.sendActionBar(player, "&4&lWARNING: &4You have chat disabled! Turn it on in your settings", Time.MINUTE.get());
				Utils.send(player, "");
				Utils.send(player, "&4&lWARNING: &4You have chat disabled! Turn it on in your settings");
				Utils.send(player, "");
			}
		});
	}

	@EventHandler
	public void onEnderDragonDeath(EntityDeathEvent event) {
		if (!event.getEntityType().equals(EntityType.ENDER_DRAGON))
			return;

		if (RandomUtils.chanceOf(33))
			event.getDrops().add(new ItemStack(Material.DRAGON_EGG));
	}

	@EventHandler
	public void onPlacePotionLauncherHopper(BlockPlaceEvent event) {
		if (!event.getBlockPlaced().getType().equals(Material.HOPPER))
			return;

		NBTItem itemNBT = new NBTItem(event.getItemInHand());
		if (!itemNBT.hasNBTData())
			return;

		if (itemNBT.asNBTString().contains("&8Potion Launcher"))
			event.setCancelled(true);
	}

	@EventHandler
	public void onBreakEmptyShulkerBox(BlockBreakEvent event) {
		if (!MaterialTag.SHULKER_BOXES.isTagged(event.getBlock().getType()))
			return;

		if (!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
			return;

		NBTTileEntity tileEntityNBT = new NBTTileEntity(event.getBlock().getState());
		if (!tileEntityNBT.asNBTString().contains("Items:[")) {
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
		}
	}

	private static final List<UUID> toSpawn = new ArrayList<>();

	@EventHandler
	public void onConnect(AsyncPlayerPreLoginEvent event) {
		Nerd nerd = new Nerd(event.getUniqueId());
		World world = nerd.getDimension();
		if (world == null) return;

		if (world.getName().startsWith("resource")) {
			nerd = new NerdService().get(event.getUniqueId());
			if (nerd.getLastQuit().isBefore(YearMonth.now().atDay(1).atStartOfDay()))
				toSpawn.add(event.getUniqueId());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (toSpawn.contains(event.getPlayer().getUniqueId())) {
			new WarpService().get("spawn", WarpType.NORMAL).teleport(event.getPlayer());
			Nexus.log("Teleporting resource world player " + event.getPlayer().getName() + " to spawn");
			toSpawn.remove(event.getPlayer().getUniqueId());
		}

		Tasks.wait(5, () -> {
			if (toSpawn.contains(event.getPlayer().getUniqueId())) {
				new WarpService().get("spawn", WarpType.NORMAL).teleport(event.getPlayer());
				Nexus.log("Teleporting resource world player " + event.getPlayer().getName() + " to spawn [2]");
				toSpawn.remove(event.getPlayer().getUniqueId());
			}

			WorldGroup worldGroup = WorldGroup.get(event.getPlayer());
			if (worldGroup == WorldGroup.MINIGAMES)
				joinMinigames(event.getPlayer());
			else if (worldGroup == WorldGroup.CREATIVE)
				joinCreative(event.getPlayer());
		});

		// Moved home for pork splegg map build
		SettingService settingService = new SettingService();
		if (event.getPlayer().getUniqueId().toString().equalsIgnoreCase("5bff3b47-06f3-4766-9468-edfe19266997")) {
			Setting setting = settingService.get(event.getPlayer(), "s6oobertTP");
			if (!setting.getBoolean()) {
				Utils.runCommand(event.getPlayer(), "home");
				setting.setBoolean(true);
				settingService.save(setting);
			}
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();

		switch (WorldGroup.get(player)) {
			case MINIGAMES:
				Tasks.wait(5, () -> joinMinigames(player));
				break;
			case CREATIVE:
				Tasks.wait(5, () -> joinCreative(player));
				break;
			case SKYBLOCK:
			case SURVIVAL:
				Tasks.wait(10, player::resetPlayerTime);
				if (WorldGroup.get(event.getFrom()).equals(WorldGroup.CREATIVE) || WorldGroup.get(event.getFrom()).equals(WorldGroup.EVENT)) {
					if (!player.hasPermission("essentials.speed"))
						Utils.runCommandAsOp(player, "flyspeed 1");
					if (!player.hasPermission("essentials.fly"))
						player.setFlying(false);
				}
				break;
		}

		if (event.getFrom().getName().equalsIgnoreCase("donortrial"))
			Tasks.wait(20, () -> {
				Utils.send(player, "Removing pets, disguises and ptime changes");
				Utils.runCommandAsConsole("undisguiseplayer " + player.getName());
				Utils.runCommandAsConsole("petadmin remove " + player.getName());
				Utils.runCommandAsConsole("mpet remove " + player.getName());
				Utils.runCommandAsOp(player, "particles stopall");
				Utils.runCommandAsOp(player, "powder cancel");
				Utils.runCommandAsConsole("speed walk 1 " + player.getName());
				player.resetPlayerTime();
			});

		if (player.getWorld().getName().equalsIgnoreCase("staff_world"))
			Tasks.wait(20, () -> Utils.runCommand(player, "cheats off"));
	}

	public void joinMinigames(Player player) {
		Utils.runCommand(player, "ch join m");
	}

	public void joinCreative(Player player) {
		Utils.runCommand(player, "ch join c");
	}

	public static class PlayerDamageByPlayerEvent extends Event {
		@NonNull
		@Getter
		final Player victim;
		@NonNull
		@Getter
		final Player attacker;
		@NonNull
		@Getter
		final EntityDamageByEntityEvent originalEvent;

		@SneakyThrows
		public PlayerDamageByPlayerEvent(Player victim, Player attacker, EntityDamageByEntityEvent event) {
			this.victim = victim;
			this.attacker = attacker;
			this.originalEvent = event;
		}

		private static final HandlerList handlers = new HandlerList();

		public static HandlerList getHandlerList() {
			return handlers;
		}

		@Override
		public HandlerList getHandlers() {
			return handlers;
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player)) return;

		Player attacker = null;
		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			Projectile projectile = (Projectile) event.getDamager();
			if (projectile.getShooter() instanceof Player)
				attacker = (Player) projectile.getShooter();
		}

		if (attacker == null) return;

		PlayerDamageByPlayerEvent newEvent = new PlayerDamageByPlayerEvent((Player) event.getEntity(), attacker, event);
		newEvent.callEvent();
	}

	// ImageOnMap rotating frames on placement; rotate back one before placement to offset
	@EventHandler
	public void onMapHang(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame))
			return;

		ItemStack tool = getTool(event.getPlayer());
		if (tool == null)
			return;

		if (tool.getType() != Material.FILLED_MAP)
			return;

		int mapId = ((MapMeta) tool.getItemMeta()).getMapId();
		if (!Paths.get("plugins/ImageOnMap/images/map" + mapId + ".png").toFile().exists())
			return;

		ItemFrame itemFrame = (ItemFrame) entity;

		if (!isNullOrAir(itemFrame.getItem()))
			return;

		itemFrame.setRotation(itemFrame.getRotation().rotateCounterClockwise());
	}

}