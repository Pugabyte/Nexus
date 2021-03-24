package me.pugabyte.nexus.features.listeners;

import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.NerdService;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class NerdListener implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getPlayer());
		nerd.setLastJoin(Utils.epochMilli(System.currentTimeMillis()));
		nerd.getPastNames().add(event.getPlayer().getName());
		service.save(nerd);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		NerdService service = new NerdService();
		Nerd nerd = Nerd.of(event.getPlayer());
		nerd.setLastQuit(Utils.epochMilli(System.currentTimeMillis()));
		nerd.getPastNames().add(event.getPlayer().getName());
		service.save(nerd);
	}

}
