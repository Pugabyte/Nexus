package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

import static gg.projecteden.nexus.utils.StringUtils.getCoordinateString;

@Permission("group.staff")
public class LocationsCommand extends CustomCommand {
	public LocationsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void execute() {
		HoursService service = new HoursService();

		line();
		Bukkit.getWorlds().forEach(world -> {
			List<Player> players = world.getPlayers();
			if (players.isEmpty())
				return;

			send("&6&l " + StringUtils.getWorldDisplayName(world));
			players.forEach(target -> {
				int playtimeSeconds = service.get(target.getUniqueId()).getTotal();
				NamedTextColor playtimeColor = playtimeSeconds <= 3600 ? NamedTextColor.RED : NamedTextColor.GRAY;

				send(json()
						.next("&f  " + Nerd.of(target).getColoredName() + "  ")
						.copy(target.getUniqueId().toString())
						.hover("&fClick to copy UUID")
						.group()
						.next(Component.text(getCoordinateString(target.getLocation()), NamedTextColor.YELLOW))
						.next(Component.text("  " + TimespanBuilder.of(playtimeSeconds).noneDisplay(true).format(), playtimeColor))
						.command("/tp " + target.getName()));
			});
		});
		line();
	}
}
