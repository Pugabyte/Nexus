package me.pugabyte.nexus.features.events.y2021.bearfair21;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;
import com.gmail.nossr50.events.skills.fishing.McMMOPlayerFishingEvent;
import me.pugabyte.nexus.Nexus;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerTakeLecternBookEvent;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;

public class Restrictions implements Listener {

	public Restrictions() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void onInteractWithVillager(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (!(entity instanceof Villager)) return;
		if (CitizensAPI.getNPCRegistry().isNPC(entity)) return;

		Player player = event.getPlayer();

		if (isAtBearFair(player))
			event.setCancelled(true);
	}

	@EventHandler
	public void onMcMMOXpGainEvent(McMMOPlayerXpGainEvent event) {
		if (!isAtBearFair(event.getPlayer())) return;
		event.setRawXpGained(0F);
		event.setCancelled(true);
	}

	@EventHandler
	public void onTameEntity(EntityTameEvent event) {
		if (!isAtBearFair(event.getEntity())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onLecternTakeBook(PlayerTakeLecternBookEvent event) {
		Location loc = event.getLectern().getBlock().getLocation();
		if (!isAtBearFair(loc)) return;

		event.setCancelled(true);
		event.getPlayer().closeInventory();
	}

	@EventHandler
	public void onMcMMOFishing(McMMOPlayerFishingEvent event) {
		if (!isAtBearFair(event.getPlayer())) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onBlockDropItemEvent(BlockDropItemEvent event) {
		if (!BearFair21.isAtBearFair(event.getBlock()))
			return;

		event.getItems().forEach(item -> {
			Material type = item.getItemStack().getType();
			if (type.equals(Material.WHEAT_SEEDS) || type.equals(Material.BEETROOT_SEEDS))
				item.remove();
		});
	}

	@EventHandler
	public void onWitherRoseDamageEvent(EntityDamageEvent event) {
		if (!BearFair21.isAtBearFair(event.getEntity()))
			return;

		if (event.getCause().equals(DamageCause.WITHER))
			event.setCancelled(true);
	}
}
