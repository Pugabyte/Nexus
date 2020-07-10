package me.pugabyte.bearnation.server.features.commands.worldedit;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldEditUtils;
import org.bukkit.entity.Player;

@DoubleSlash
@Permission("worldedit.wand")
public class ExpandAllCommand extends CustomCommand {

	public ExpandAllCommand(CommandEvent event) {
		super(event);
	}

	@Path("[amount]")
	void expandAll(@Arg("1") int amount) {
		expandAll(player(), amount);
	}

	static void expandAll(Player player, int amount) {
		new WorldEditUtils(player).changeSelection(
				player,
				WorldEditUtils.SelectionChangeType.EXPAND,
				WorldEditUtils.SelectionChangeDirectionType.ALL,
				amount
		);
	}
}

