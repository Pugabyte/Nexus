package gg.projecteden.nexus.features.mcmmo;

import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import com.gmail.nossr50.util.player.UserManager;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.BlockUtils;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGroup;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.TreeSpecies;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Sapling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

public class McMMOListener implements Listener {

	public McMMOListener() {
		Nexus.registerListener(this);
		scheduler();
	}

	@EventHandler
	public void onMcMMOLevelUp(McMMOPlayerLevelUpEvent event) {
		if (event.getSkillLevel() == 100)
			Koda.say(Nickname.of(event.getPlayer()) + " reached level 100 in " + camelCase(event.getSkill().name()) + "! Congratulations!");
		if (UserManager.getOfflinePlayer(event.getPlayer()).getPowerLevel() == 1300)
			Koda.say(Nickname.of(event.getPlayer()) + " has mastered all their skills! Congratulations!");
	}

	void scheduler() {
		Tasks.repeat(0, Time.SECOND.x(1), () -> {
			PlayerUtils.getOnlinePlayers().forEach(player -> {
				if (!canBootBonemeal(player))
					return;

				// Loop all blocks in radius x of player
				Location playerLoc = player.getLocation();
				int radius = 5;
				List<Block> blocksNearby = BlockUtils.getBlocksInRadius(playerLoc, radius);
				for (Block block : blocksNearby) {
					if (RandomUtils.chanceOf(80))
						continue;

					Material blockType = block.getType();

					if (blockType.equals(Material.FARMLAND) || blockType.equals(Material.COCOA)) {
						Block crop = block.getRelative(0, 1, 0);
						if (blockType.equals(Material.COCOA))
							crop = block;
						if (!growCrop(crop)) continue;
						showParticle(player, crop.getLocation());
					} else if (MaterialTag.SAPLINGS.isTagged(blockType)
							|| Arrays.asList(Material.BROWN_MUSHROOM, Material.RED_MUSHROOM).contains(blockType)) {
						if (!growTree(block)) continue;
						showParticle(player, block.getLocation());
					} else if (blockType.equals(Material.SUGAR_CANE) || blockType.equals(Material.CACTUS)) {
						if (!growMulti(block)) continue;
						showParticle(player, block.getRelative(0, 1, 0).getLocation());
					}
				}
			});
		});
	}

	boolean canBootBonemeal(Player player) {
		// If player is wearing boots
		if (ItemUtils.isNullOrAir(player.getInventory().getBoots()))
			return false;

		// If player is wearing gold boots
		if (!player.getInventory().getBoots().getType().equals(Material.GOLDEN_BOOTS))
			return false;

		// if player is in survival
		WorldGroup world = WorldGroup.of(player);
		if (!world.equals(WorldGroup.SURVIVAL))
			return false;

		// if boots has lore
		ItemStack boots = player.getInventory().getBoots();
		ItemMeta meta = boots.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			return false;

		// if lore on boots contains "bonemeal boots"
		if (!(String.join(",", lore).contains("Bonemeal Boots")))
			return false;

		return true;
	}

	boolean growCrop(Block block) {
		BlockData blockData = block.getBlockData();
		if (!(blockData instanceof Ageable ageable)) return false;

		int maxAge = ageable.getMaximumAge();
		int age = ageable.getAge();
		if (age == maxAge) {
			if (block.getType().equals(Material.MELON_STEM) || block.getType().equals(Material.PUMPKIN_STEM)) {
				List<BlockFace> cardinals = Arrays.asList(BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST);

				Material placeType = Material.PUMPKIN;
				Material stemType = Material.ATTACHED_PUMPKIN_STEM;
				if (block.getType().equals(Material.MELON_STEM)) {
					placeType = Material.MELON;
					stemType = Material.ATTACHED_MELON_STEM;
				}

				for (BlockFace cardinal : cardinals) {
					Material cardinalType = block.getRelative(cardinal).getType();
					if (cardinalType.equals(placeType))
						return false;
				}

				List<BlockFace> possibleFaces = new ArrayList<>();
				for (BlockFace cardinal : cardinals) {
					Block cardinalBlock = block.getRelative(cardinal);
					Material below = cardinalBlock.getRelative(0, -1, 0).getType();
					MaterialTag growBlocks = new MaterialTag(MaterialTag.ALL_DIRT).exclude(Material.DIRT_PATH);
					if (growBlocks.isTagged(below) && cardinalBlock.getType().equals(Material.AIR))
						possibleFaces.add(cardinal);
				}

				if (possibleFaces.size() == 0)
					return false;

				BlockFace randomFace = RandomUtils.randomElement(possibleFaces);
				block.getRelative(randomFace).setType(placeType);
				block.setType(stemType);
				blockData = block.getBlockData();
				Directional directional = (Directional) blockData;
				directional.setFacing(randomFace);
				block.setBlockData(directional);

				return true;
			} else
				return false;
		}
		++age;
		ageable.setAge(age);
		block.setBlockData(ageable);

		return true;
	}

	boolean growTree(Block block) {
		Material blockType = block.getType();
		TreeType treeType = getTreeType(block.getLocation());

		if (treeType == null) return false;

		Location treeLoc = block.getLocation();
		Location megaLoc = getMegaTree(block);

		if (megaLoc != null) {
			treeType = getMegaVariant(treeType);
			treeLoc = megaLoc;
		} else {
			if (RandomUtils.chanceOf(20))
				treeType = getVariant(treeType);
		}

		boolean isSapling = Tag.SAPLINGS.isTagged(blockType);
		block.setType(Material.AIR);
		if (!block.getWorld().generateTree(treeLoc, treeType)) {
			block.setType(blockType);
			if (isSapling) {
				Sapling sapling = (Sapling) block.getState().getData();
				block.getState().getData().setData(sapling.getSpecies().getData());
			}
			return false;
		}
		return true;
	}

	boolean growMulti(Block block) {
		Material blockType = block.getType();

		// Find the bottom most block
		Block ground = block.getRelative(0, -1, 0);
		if (ground.getType().equals(blockType)) {
			ground = block.getRelative(0, -2, 0);
			if (ground.getType().equals(blockType))
				ground = block.getRelative(0, -3, 0);
		}

		if (ground.getType().equals(blockType)) return false;
		Location groundLoc = ground.getLocation();

		Block above = block.getRelative(0, 1, 0);
		if (groundLoc.distance(above.getLocation()) > 3) return false;

		// If the block above is air or same material
		if ((above.getType().equals(Material.AIR) || above.getType().equals(blockType))) {
			if (!above.getType().equals(Material.AIR)) {
				above = above.getRelative(0, 1, 0);
				if (groundLoc.distance(above.getLocation()) > 3)
					return false;
			}

			if (above.getType().equals(Material.AIR)) {
				above.setType(blockType);
			} else {
				return false;
			}
		}
		return true;
	}

	void showParticle(Player player, Location location) {
		if (RandomUtils.chanceOf(50))
			player.spawnParticle(Particle.VILLAGER_HAPPY, location, 5, 0.5, 0.5, 0.5, 0.01);
	}

	private TreeType getTreeType(TreeSpecies treeSpecies) {
		return switch (treeSpecies.getData()) {
			case 0 -> TreeType.TREE;
			case 1 -> TreeType.REDWOOD;
			case 2 -> TreeType.BIRCH;
			case 3 -> TreeType.SMALL_JUNGLE;
			case 4 -> TreeType.ACACIA;
			case 5 -> TreeType.DARK_OAK;
			default -> null;
		};
	}

	private TreeType getVariant(TreeType treeType) {
		return switch (treeType) {
			case TREE -> TreeType.BIG_TREE;
			case BIRCH -> TreeType.TALL_BIRCH;
			case REDWOOD -> TreeType.TALL_REDWOOD;
			case SMALL_JUNGLE -> TreeType.COCOA_TREE;
			default -> treeType;
		};
	}

	private TreeType getMegaVariant(TreeType treeType) {
		return switch (treeType) {
			case REDWOOD -> TreeType.MEGA_REDWOOD;
			case SMALL_JUNGLE -> TreeType.JUNGLE;
			default -> treeType;
		};
	}

	private Location getMegaTree(Block block) {
		if (!Tag.SAPLINGS.isTagged(block.getType()))
			return null;

		Location start = block.getLocation();

		if (start.getX() < 0) start.add(.5, 0, 0);
		if (start.getZ() < 0) start.add(0, 0, .5);

		TreeType treeType = getTreeType(start);
		if (treeType == null)
			return null;

		List<BlockFace> ordinals = Arrays.asList(BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST, BlockFace.NORTH_EAST, BlockFace.SOUTH_EAST);

		Location northwest = null;
		for (BlockFace blockFace : ordinals) {
			Location corner = start.clone().getBlock().getRelative(blockFace).getLocation();

			if (treeType != getTreeType(corner))
				continue;

			Location x = start.clone().add(blockFace.getModX(), 0, 0);
			Location z = start.clone().add(0, 0, blockFace.getModZ());

			if (getTreeType(x) == treeType && getTreeType(z) == treeType) {
				int minX = (int) Math.min(start.getX(), x.getX());
				int minZ = (int) Math.min(start.getZ(), z.getZ());

				northwest = new Location(start.getWorld(), minX, start.getY(), minZ);
				northwest = LocationUtils.getBlockCenter(northwest);
				break;
			}
		}
		return northwest;
	}

	private TreeType getTreeType(Location location) {
		if ((location.getBlock().getState().getData() instanceof Sapling sapling)) {
			return getTreeType(sapling.getSpecies());
		} else if (location.getBlock().getType().equals(Material.BROWN_MUSHROOM)) {
			return TreeType.BROWN_MUSHROOM;
		} else if (location.getBlock().getType().equals(Material.RED_MUSHROOM)) {
			return TreeType.RED_MUSHROOM;
		}
		return null;
	}
}
