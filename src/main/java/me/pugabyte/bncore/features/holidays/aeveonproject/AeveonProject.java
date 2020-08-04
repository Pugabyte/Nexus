package me.pugabyte.bncore.features.holidays.aeveonproject;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Data;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.aeveonproject.effects.Effects;
import me.pugabyte.bncore.features.holidays.aeveonproject.sets.SetType;
import me.pugabyte.bncore.utils.Time.Timer;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class AeveonProject implements Listener {
	@Getter
	public static final World WORLD = Bukkit.getWorld("Aeveon_Project");
	@Getter
	public static final WorldGuardUtils WGUtils = new WorldGuardUtils(WORLD);
	public static final WorldEditUtils WEUtils = new WorldEditUtils(WORLD);

	public static String PREFIX = "&8&l[&eAeveonProject&8&l] &3";
	public static String ROOT = "Animations/AeveonProject/";

	public AeveonProject() {
		BNCore.registerListener(this);
		new Timer("    Effects", Effects::new);
		new Timer("    Sets", SetType::values);
	}

	public static boolean isInSpace(Player player) {
		Set<ProtectedRegion> regions = WGUtils.getRegionsAt(player.getLocation());
		Set<ProtectedRegion> spaceRegions = regions.stream().filter(region -> region.getId().contains("space")).collect(Collectors.toSet());
		return spaceRegions.size() > 0;
	}

	public static boolean isInWorld(Block block) {
		return isInWorld(block.getLocation());
	}

	public static boolean isInWorld(Player player) {
		return isInWorld(player.getLocation());
	}

	public static boolean isInWorld(Location location) {
		return location.getWorld().equals(AeveonProject.getWORLD());

	}
}
