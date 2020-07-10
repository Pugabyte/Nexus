package me.pugabyte.bearnation.api.models.nerd;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bearnation.api.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public class NerdListener implements Listener {
	@NonNull
	private Plugin plugin;

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		NerdService service = new NerdService(plugin);
		Nerd nerd = service.get(event.getPlayer());
		nerd.setLastJoin(Utils.epochMilli(System.currentTimeMillis()));
		service.save(nerd);
		service.addPastName(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		NerdService service = new NerdService(plugin);
		Nerd nerd = service.get(event.getPlayer());
		nerd.setLastQuit(Utils.epochMilli(System.currentTimeMillis()));
		service.save(nerd);
	}

}
