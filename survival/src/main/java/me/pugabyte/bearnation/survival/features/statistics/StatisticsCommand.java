package me.pugabyte.bearnation.survival.features.statistics;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;

@Aliases("stats")
@Permission("group.admin")
public class StatisticsCommand extends CustomCommand {

	public static List<Material> blockCache = new ArrayList<>();

	public StatisticsCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void check(@Arg("self") OfflinePlayer player) {
		StatisticsMenu.open(player(), StatisticsMenu.StatsMenus.MAIN, 0, player);
	}

}
