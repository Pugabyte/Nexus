package me.pugabyte.bearnation.server.features.commands.staff;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bearnation.BNCore;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.stream.Collectors;

@Aliases("wgedit")
@NoArgsConstructor
@Permission("group.staff")
public class WorldGuardEditCommand extends CustomCommand implements Listener {
	@Getter
	public static String permission = "worldguard.region.bypass.*";

	public WorldGuardEditCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null) enable = !player().hasPermission(permission);

		if (enable)
			on();
		else
			off();
	}

	private void on() {
		BNCore.getPerms().playerAdd(player(), permission);
		send("&eWorldGuard editing &aenabled");
	}

	private void off() {
		BNCore.getPerms().playerRemove(player(), permission);
		send("&eWorldGuard editing &cdisabled");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasPermission(permission))
			BNCore.getPerms().playerRemove(event.getPlayer(), permission);
	}

	@ConverterFor(Flag.class)
	Flag<?> convertToWorldGuardFlag(String value) {
		return WorldGuard.getInstance().getFlagRegistry().get(value);
	}

	@TabCompleterFor(Flag.class)
	List<String> tabCompleteWorldGuardFlag(String filter) {
		return WorldGuard.getInstance().getFlagRegistry().getAll().stream()
				.map(Flag::getName)
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

}
