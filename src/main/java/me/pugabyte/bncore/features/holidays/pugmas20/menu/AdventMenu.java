package me.pugabyte.bncore.features.holidays.pugmas20.menu;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.SlotPos;
import lombok.Getter;
import me.pugabyte.bncore.features.holidays.pugmas20.Pugmas20;
import me.pugabyte.bncore.features.holidays.pugmas20.menu.providers.AdventProvider;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public class AdventMenu {
	//TODO PUGMAS - Change to final location
	private static final Location adventHeadsLoc = new Location(Pugmas20.world, -956, 9, -2096);
	public static final Block origin = adventHeadsLoc.getBlock().getRelative(BlockFace.UP);
	@Getter
	private static final LinkedHashMap<SlotPos, ItemBuilder> adventHeadMap = new LinkedHashMap<>();
	public static ItemBuilder lockedHead;
	public static ItemBuilder missedHead;
	public static ItemBuilder toFindHead;


	public static void loadHeads() {
		// Specific Heads
		origin.getRelative(0, 0, 2).getDrops().stream().findFirst().ifPresent(skull -> lockedHead = new ItemBuilder(skull));
		origin.getRelative(0, 0, 3).getDrops().stream().findFirst().ifPresent(skull -> missedHead = new ItemBuilder(skull));
		origin.getRelative(0, 0, 4).getDrops().stream().findFirst().ifPresent(skull -> toFindHead = new ItemBuilder(skull));

		// Advent menu
		String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturaday"};

		for (int z = 0; z <= 4; z++) {        // 0-4 col
			for (int x = 1; x <= 7; x++) {    // 1-7 row
				Block block = origin.getRelative(x, 0, z);
				if (!Utils.isNullOrAir(block)) {
					ItemStack drop = block.getDrops().stream().findFirst().orElse(null);
					if (!Utils.isNullOrAir(drop)) {
						ItemBuilder skull = new ItemBuilder(drop);
						int size = adventHeadMap.size();
						if (size <= 6)
							skull.name(days[size]);

						adventHeadMap.put(new SlotPos(z, x), skull);
					}
				}
			}
		}
	}

	public static void openAdvent(Player player, int day) {
		SmartInventory.builder()
				.title("Advent")
				.size(6, 9)
				.provider(new AdventProvider(day))
				.build()
				.open(player);
	}
}
