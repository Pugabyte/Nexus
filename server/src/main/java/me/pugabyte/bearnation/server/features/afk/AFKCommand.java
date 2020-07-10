package me.pugabyte.bearnation.server.features.afk;

import lombok.NoArgsConstructor;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.server.models.afk.AFKPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Aliases("away")
@NoArgsConstructor
public class AFKCommand extends CustomCommand implements Listener {

	public AFKCommand(CommandEvent event) {
		super(event);
	}

	@Path("[autoreply...]")
	void afk(String autoreply) {
		AFKPlayer player = AFK.get(player());

		if (player.isAfk())
			player.notAfk();
		else {
			player.setMessage(autoreply);
			player.setForceAfk(true);
			player.afk();
			tasks().wait(Time.SECOND.x(10), () -> {
				player.setLocation();
				player.setForceAfk(false);
			});
		}
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		tasks().wait(3, () -> {
			if (!event.getPlayer().isOnline()) return;

			AFKPlayer player = AFK.get(event.getPlayer());
			if (player.isAfk() && !player.isForceAfk())
				player.notAfk();
		});
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		AFK.remove(event.getPlayer());
	}

}
