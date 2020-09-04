package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.APUtils;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSet;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.APSetType;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectService;
import me.pugabyte.bncore.models.aeveonproject.AeveonProjectUser;
import me.pugabyte.bncore.utils.ColorType;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Collection;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.aeveonproject.APUtils.getPlayersInAPWorld;
import static me.pugabyte.bncore.features.holidays.aeveonproject.APUtils.getPlayersInSet;
import static me.pugabyte.bncore.features.holidays.aeveonproject.APUtils.getShipColorRegion;
import static me.pugabyte.bncore.features.holidays.aeveonproject.APUtils.isInWorld;
import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WEUtils;
import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.WGUtils;

public class ClientsideBlocks implements Listener {
	private static final AeveonProjectService service = new AeveonProjectService();
	private final int minBoundary = 20;
	private final int maxBoundary = 40;

	public ClientsideBlocks() {
		BNCore.registerListener(this);

		updateTask();
	}

	private void updateTask() {
		Tasks.repeat(0, Time.SECOND.x(2), () -> {
			if (getPlayersInAPWorld() == 0)
				return;

			for (APSetType APSetType : APSetType.values()) {
				APSet set = APSetType.get();
				List<String> updateRegions = set.getUpdateRegions();
				if (updateRegions == null)
					continue;

				Collection<Player> setPlayers = getPlayersInSet(set);
				if (setPlayers.size() == 0)
					continue;

				for (String updateRegion : updateRegions) {
					Region region = WGUtils.getRegion(updateRegion);
					Location rgCenter = WEUtils.toLocation(region.getCenter());
					double rgBoundary = Math.max(Math.min(Math.max(region.getWidth(), region.getLength()), maxBoundary), minBoundary);

					for (Player player : setPlayers) {
						if (!APUtils.isInWorld(player))
							continue;

						double distance = player.getLocation().distance(rgCenter);
						if (distance <= rgBoundary)
							update(player, updateRegion);
					}
				}
			}
		});
	}

	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		update(player, event.getRegion());
	}

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		update(player);
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		update(player);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!isInWorld(event.getTo()))
			return;

		update(event.getPlayer(), event.getTo());
	}

	//

	public static void update(Player player, ProtectedRegion region) {
		APSet set = APSetType.getFromRegion(region);
		if (set != null)
			update(player, set);
	}

	public static void update(Player player) {
		update(player, player.getLocation());
	}

	public static void update(Player player, Location location) {
		APSet set = APSetType.getFromLocation(location);
		if (set != null)
			update(player, set);
	}

	//

	public static void update(Player player, APSet set) {
		for (String updateRegion : set.getUpdateRegions()) {
			update(player, updateRegion);
		}
	}

	public static void update(Player player, String region) {
		if (!service.hasStarted(player)) return;
		AeveonProjectUser user = service.get(player);

		// Any Ship Color Region
		if (region.contains("shipcolor")) {
			Color shipColor = user.getShipColor();
			ColorType colorType;

			Material concreteType = Material.BLACK_CONCRETE;
			Material bedType = Material.BLACK_BED;

			if (shipColor != null) {
				colorType = ColorType.of(shipColor);

				if (colorType.getConcrete() != null)
					concreteType = colorType.getConcrete();

				if (colorType.getBed() != null)
					bedType = colorType.getBed();

			}

			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(getShipColorRegion(region)));

			for (Block block : blocks) {
				if (block.getType().equals(Material.WHITE_CONCRETE))
					user.getPlayer().sendBlockChange(block.getLocation(), concreteType.createBlockData());

				else if (block.getType().equals(Material.WHITE_BED)) {
					BlockData blockData = bedType.createBlockData();

					Bed newBed = (Bed) blockData;
					Bed oldBed = (Bed) block.getBlockData();

					if (oldBed.getPart().equals(Bed.Part.HEAD))
						newBed.setPart(Bed.Part.HEAD);
					else
						newBed.setPart(Bed.Part.FOOT);

					newBed.setFacing(oldBed.getFacing());

					blockData = newBed;
					user.getPlayer().sendBlockChange(block.getLocation(), blockData);
				}
			}
		}

		// Any Docking Ports Region
		if (region.contains("dockingport")) {
			List<Block> blocks = WEUtils.getBlocks(WGUtils.getRegion(region));
			for (Block block : blocks) {
				if (block.getType().equals(Material.WATER))
					player.sendBlockChange(block.getLocation(), Material.AIR.createBlockData());
			}
		}
	}
}
