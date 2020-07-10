package me.pugabyte.bearnation.server.features.holidays.bearfair20.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Utils;
import org.bukkit.Material;

import java.util.concurrent.atomic.AtomicInteger;

@Permission("group.admin")
@Aliases("bfreplaceyellowwoolwithhoneycombblock")
public class RYWHCCommand extends CustomCommand {

	public RYWHCCommand(CommandEvent event) {
		super(event);
	}

	@Path("[range]")
	void range(@Arg("25") int range) {
		if (range > 50) {
			send(PREFIX + "Max range is 50");
			range = 50;
		}
		send("Working with a range of " + range + "...");
		AtomicInteger amount = new AtomicInteger(0);
		Utils.getBlocksInRadius(player().getLocation(), range).forEach(block -> {
			if (block.getType() == Material.YELLOW_WOOL) {
				block.setType(Material.HONEYCOMB_BLOCK);
				amount.incrementAndGet();
			}
		});
		send(PREFIX + "Done. Replaced &e" + amount.get() + " &3blocks");
	}

}
