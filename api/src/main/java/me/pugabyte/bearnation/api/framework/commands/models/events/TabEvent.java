package me.pugabyte.bearnation.api.framework.commands.models.events;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.exceptions.preconfigured.NoPermissionException;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class TabEvent extends CommandEvent {

	public TabEvent(CommandSender sender, CustomCommand command, Plugin plugin, String aliasUsed, List<String> args) {
		super(sender, command, plugin, aliasUsed, args);
	}

	@Override
	public void handleException(Throwable ex) {
		if (ex instanceof NoPermissionException)
			return;
		ex.printStackTrace();
	}

}
