package me.pugabyte.bncore.features.holidays.bearfair20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Halloween;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MinigameNight;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bncore.utils.Utils.isNullOrAir;
import static me.pugabyte.bncore.utils.Utils.isVanished;

/*
	Easter Egg Spots:
		-939 137 -1624
		-1155 141 -1777
		-902 133 -1637
		-991 135 -1621
		-1095 155 -1559
		-1101 137 -1651
		-944 119 -1895
		-1050 129 -1913
	On find all:
	/playsound minecraft:ui.toast.challenge_complete master @p ~ ~ ~ 2 1
 */

@Data
public class BearFair20 implements Listener {

	public static World world = Bukkit.getWorld("safepvp");
	public static WorldGuardUtils WGUtils = new WorldGuardUtils(world);
	public static String BFRg = "bearfair2020";
	public static ProtectedRegion BFProtectedRg = WGUtils.getProtectedRegion(BearFair20.BFRg);

	public BearFair20() {
		BNCore.registerListener(this);
		new Timer("    Fairgrounds", Fairgrounds::new);
		new Timer("    Halloween", Halloween::new);
		new Timer("    MinigameNight", MinigameNight::new);
		new Timer("    BFQuests", BFQuests::new);
	}

	public static String isCheatingMsg(Player player) {
		if (player.hasPermission("worldguard.region.bypass.*")) return "wgedit";
		if (!player.getGameMode().equals(GameMode.SURVIVAL)) return "creative";
		if (player.isFlying()) return "fly";
		if (isVanished(player)) return "vanish";
		if (BNCore.getEssentials().getUser(player.getUniqueId()).isGodModeEnabled()) return "godmode";

		return null;
	}

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		Location loc = event.getEntity().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BFRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BFRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;
//		if (player.hasPermission("worldguard.region.bypass.*")) {
//			Utils.runCommand(player, "wgedit off");
//		}

	}

	@EventHandler
	public void onThrowEnderPearl(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Location loc = player.getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BFRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;

		if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!isNullOrAir(item)) {
				if (item.getType().equals(Material.ENDER_PEARL)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onLecternTakeBook(PlayerTakeLecternBookEvent event) {
		Location loc = event.getLectern().getBlock().getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BFRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;

		event.setCancelled(true);
		event.getPlayer().closeInventory();
	}

	@EventHandler
	public void onExitMinecart(VehicleExitEvent event) {
		if (!(event.getExited() instanceof Player)) return;
		if (!(event.getVehicle() instanceof Minecart)) return;

		Player player = (Player) event.getExited();
		Location loc = player.getLocation();
		ProtectedRegion region = WGUtils.getProtectedRegion(BFRg);
		if (!WGUtils.getRegionsAt(loc).contains(region)) return;

		Tasks.wait(1, () -> {
			event.getVehicle().remove();
			Fairgrounds.giveKit(Fairgrounds.BearFairKit.MINECART, player);
		});
	}

}
