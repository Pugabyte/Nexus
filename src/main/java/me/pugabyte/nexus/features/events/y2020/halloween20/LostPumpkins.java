package me.pugabyte.nexus.features.events.y2020.halloween20;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2020.halloween20.models.Pumpkin;
import me.pugabyte.nexus.features.events.y2020.halloween20.models.QuestStage;
import me.pugabyte.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import me.pugabyte.nexus.models.halloween20.Halloween20Service;
import me.pugabyte.nexus.models.halloween20.Halloween20User;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.PacketUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class LostPumpkins implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("LostPumpkins");

	public LostPumpkins() {
		Nexus.registerListener(this);
		startParticleTask();
	}

	private void startParticleTask() {
		Tasks.repeatAsync(0, 2 * 20, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getWorld() != Halloween20.getWorld())
					continue;

				Halloween20User user = new Halloween20Service().get(player);
				for (Pumpkin pumpkin : Pumpkin.values()) {
					if (user.getFoundPumpkins().contains(pumpkin.getOriginal())) continue;
					player.spawnParticle(Particle.REDSTONE, LocationUtils.getCenteredLocation(pumpkin.getOriginal()), 5, .5, .5, .5, new DustOptions(Color.ORANGE, 1));
				}
			}
		});
	}

	// Finding Pumpkins
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		Pumpkin pumpkin = Pumpkin.getByLocation(event.getClickedBlock().getLocation());
		if (pumpkin == null) return;
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(event.getPlayer());
		if (user.getLostPumpkinsStage() == QuestStage.LostPumpkins.NOT_STARTED) {
			user.send(PREFIX + "This looks like it should be in the pumpkin carving contest. Maybe I should talk to &eJeffery &3at the pumpkin carving contest.");
			return;
		}
		if (user.getFoundPumpkins().contains(event.getClickedBlock().getLocation())) {
			user.send(PREFIX + "&cYou have already found that pumpkin");
			return;
		}
		user.getFoundPumpkins().add(event.getClickedBlock().getLocation());
		Tasks.wait(1, () -> event.getPlayer().sendBlockChange(event.getClickedBlock().getLocation(), Material.AIR.createBlockData()));
		PacketUtils.copyTileEntityClient(event.getPlayer(), pumpkin.getOriginal().getBlock(), pumpkin.getEnd());
		user.send(PREFIX + "You have found a pumpkin! It has been returned to Jeffery. &e(" + user.getFoundPumpkins().size() + "/8)");
		service.save(user);
		if (user.getFoundPumpkins().size() == 8) {
			user.send(PREFIX + "You have found the last pumpkin! Talk to &eJeffery &3at the pumpkin carving contest.");
			user.setLostPumpkinsStage(QuestStage.LostPumpkins.FOUND_ALL);
			service.save(user);
		}
	}

	// Update Pumpkins Per User
	@EventHandler
	public void onEnterRegion(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!event.getRegion().getId().equalsIgnoreCase(Halloween20.getRegion())) return;
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		for (Location loc : user.getFoundPumpkins()) {
			Pumpkin pumpkin = Pumpkin.getByLocation(loc);
			if (pumpkin == null) continue;
			event.getPlayer().sendBlockChange(pumpkin.getOriginal(), Material.AIR.createBlockData());
			PacketUtils.copyTileEntityClient(event.getPlayer(), pumpkin.getOriginal().getBlock(), pumpkin.getEnd());
		}
	}


}
