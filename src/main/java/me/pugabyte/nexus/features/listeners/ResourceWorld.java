package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.homes.HomesFeature;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.models.tip.Tip;
import me.pugabyte.nexus.models.tip.Tip.TipType;
import me.pugabyte.nexus.models.tip.TipService;
import me.pugabyte.nexus.models.warps.Warp;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Utils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldType;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class ResourceWorld implements Listener {

	@EventHandler
	public void onEnterResourceWorld(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (event.getFrom().getWorld().getName().startsWith("resource")) return;

		if (event.getTo().getWorld().getName().startsWith("resource")) {
			if (!WorldGroup.get(event.getFrom().getWorld()).equals(WorldGroup.SURVIVAL) || event.getFrom().getWorld().getName().startsWith("staff")) {
				Utils.send(player, "&eYou can only enter the resource world from the Survival world");
				event.setCancelled(true);
				return;
			}
			List<Material> materials = new ShopService().getMarket().getProducts(Shop.ShopGroup.RESOURCE).stream()
					.filter(product -> product.getExchangeType() == Shop.ExchangeType.BUY)
					.map(product -> product.getItem().getType())
					.collect(Collectors.toList());

			// Crafting materials
			materials.add(Material.CLAY_BALL);
			materials.add(Material.GRAVEL);
			materials.add(Material.GLOWSTONE_DUST);
			materials.add(Material.ICE);
			materials.add(Material.PACKED_ICE);

			ArrayList<Material> rejectedMaterials = new ArrayList<>();
			boolean appendMessage = false;

			for (Material material : materials) {
				if (player.getInventory().contains(material)) {
					rejectedMaterials.add(material);
					event.setCancelled(true);
					appendMessage = true;
				}
			}

			if (rejectedMaterials.size() != 0) {
				Utils.send(player, "&cYou can not go to the resource world with the below items, " +
						"please remove them from your inventory before continuing:");
				for (Material material : rejectedMaterials) {
					Utils.send(player, "&e- " + camelCase(material.name()));
				}
			}

			rejectedMaterials.clear();

			ItemStack[] items = player.getInventory().getContents();
			for (ItemStack item : items) {
				if (item == null || ItemUtils.isNullOrAir(item.getType())) continue;
				if (!MaterialTag.SHULKER_BOXES.isTagged(item.getType())) continue;
				if (!(item.getItemMeta() instanceof BlockStateMeta)) continue;

				ShulkerBox shulkerBox = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();
				ItemStack[] contents = shulkerBox.getInventory().getContents();
				for (ItemStack content : contents) {
					if (content == null || ItemUtils.isNullOrAir(content.getType())) continue;
					if (materials.contains(content.getType())) {
						rejectedMaterials.add(content.getType());
						event.setCancelled(true);
					}
				}
			}

			if (rejectedMaterials.size() != 0) {
				if (appendMessage) {
					for (Material material : rejectedMaterials) {
						Utils.send(player, "&e- " + camelCase(material.name()) + " (in shulkerbox)");
					}
				} else {
					Utils.send(player, "&cYou can not go to the resource world with the below items, " +
							"please remove them from your shulkerbox before continuing:");
					for (Material material : rejectedMaterials) {
						Utils.send(player, "&e- " + camelCase(material.name()) + " (in shulkerbox)");
					}
				}
			}

			if (!event.isCancelled()) {
				Utils.send(player, " &4Warning: &cYou are entering the resource world! This world is regenerated on the " +
						"&c&lfirst of every month, &cso don't leave your stuff here or you will lose it!");
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.getPlayer().getWorld().getName().startsWith("resource")) return;

		List<Material> materials = new ArrayList<>(Arrays.asList(Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BARREL));
		materials.addAll(MaterialTag.WOODEN_DOORS.getValues());
		if (!materials.contains(event.getBlockPlaced().getType()))
			return;

		Tip tip = new TipService().get(event.getPlayer());
		if (tip.show(TipType.RESOURCE_WORLD_STORAGE))
			Utils.send(event.getPlayer(), " &4Warning: &cYou are currently building in the resource world! " +
					"This world is regenerated on the &c&lfirst of every month, &cso don't leave your stuff here or you will lose it!");
	}

	@EventHandler
	public void onOpenEnderChest(InventoryOpenEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return;
		Player player = (Player) event.getPlayer();

		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			event.setCancelled(true);
			Utils.send(player, "&cYou can't open your enderchest while in the resource world, due to restrictions in place to keep the /market balanced");
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		if (event.getPlayer().getWorld().getName().startsWith("resource")) {
			switch (event.getMessage().split(" ")[0].replace("playervaults:", "")) {
				case "/pv":
				case "/vc":
				case "/chest":
				case "/vault":
				case "/playervaults":
					event.setCancelled(true);
					Utils.send(event.getPlayer(), "&cYou cannot use vaults while in the resource world");
			}
		}
	}

	/* Find protections from people being dumb

	select
		nerd.name,
		lwc_blocks.name,
		CONCAT("/tppos ", x, " ", y, " ", z, " ", world)
	from bearnation_smp_lwc.lwc_protections
	inner join bearnation_smp_lwc.lwc_blocks
		on lwc_blocks.id = lwc_protections.blockId
	inner join bearnation.nerd
		on lwc_protections.owner = nerd.uuid
	where world in ('resource', 'resource_nether', 'resource_the_end')
		and lwc_blocks.name not like "%DOOR%"
		and lwc_blocks.name not like "%GATE%";

	 */

	// TODO Automation
	/*
	- #unload all 3 worlds
	- #move the directories to old_<world>
	- #remove uuid.dat
	- #delete homes
	- #create new worlds
	- paste spawn (y = 150)
	- #mv setspawn
	- clean light
	- #create npc for filid
	- #set world border
	- #fill chunks
	- #dynamap purge
	- #delete from bearnation_smp_lwc.lwc_protections where world in ('resource', 'resource_nether', 'resource_the_end');
	*/

	private static final int filidId = 2766;
	private static final int radius = 7500;

	public static void reset(boolean test) {
		test = true;

		NPC filid = CitizensAPI.getNPCRegistry().getById(filidId);
		filid.despawn();

		for (String worldName : Arrays.asList("resource", "resource_nether", "resource_the_end")) {
			if (test)
				worldName = "test_" + worldName;
			final String finalWorldName = worldName;

			String root = new File(".").getAbsolutePath().replace(".", "");
			File worldFolder = Paths.get(root + worldName).toFile();
			File newFolder = Paths.get(root + "old_ " + worldName).toFile();

			World world = Bukkit.getWorld(worldName);
			if (world == null)
				Nexus.severe("World " + finalWorldName + " not loaded");

			try {
				Nexus.getMultiverseCore().getMVWorldManager().unloadWorld(worldName);
			} catch (Exception ex) {
				Nexus.severe("Error unloading world " + worldName);
				ex.printStackTrace();
			}

			boolean renameSuccess = worldFolder.renameTo(newFolder);
			if (!renameSuccess) {
				Nexus.severe("Could not rename " + finalWorldName + " folder");
				return;
			}

			boolean deleteSuccess = Paths.get(newFolder.getAbsolutePath() + "/uid.dat").toFile().delete();
			if (!deleteSuccess)
				Nexus.severe("Could not delete " + finalWorldName + " uid.dat file");

			HomesFeature.deleteFromWorld(worldName, null);

			Environment env = Environment.NORMAL;
			String seed = null;
			if (worldName.contains("nether"))
				env = Environment.NETHER;
			else if (worldName.contains("the_end"))
				env = Environment.THE_END;
			else
				// TODO List of approved seeds
				seed = null;

			Nexus.getMultiverseCore().getMVWorldManager().addWorld(worldName, env, seed, WorldType.NORMAL, true, null);
		}

		String worldName = (test ? "test_" : "") + "resource";

		new WorldEditUtils(worldName).paster()
				.file("resource-world-spawn")
				.at(new Location(Bukkit.getWorld(worldName), 0, 0, 0))
				.air(false)
				.paste();

		Warp warp = new WarpService().get(worldName, WarpType.NORMAL);
		Nexus.getMultiverseCore().getMVWorldManager().getMVWorld(worldName).setSpawnLocation(warp.getLocation());
		filid.spawn(new Location(Bukkit.getWorld(worldName), .5, 151, -36.5, 0F, 0F));

		Utils.runCommandAsConsole("wb " + worldName + " set " + radius + " 0 0");
		Utils.runCommandAsConsole("bluemap purge " + worldName);
		Utils.runCommandAsConsole("chunkmaster generate " + worldName + " " + (radius + 200) + " circle");
	}

}