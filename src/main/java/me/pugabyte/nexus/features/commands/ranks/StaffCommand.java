package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Aliases({"stafflist"})
public class StaffCommand extends CustomCommand {

	public StaffCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void staff() {
		line();
		List<Rank> ranks = Rank.getStaff();
		Collections.reverse(ranks);
		ranks.forEach(rank -> send(rank.withColor() + "&f:&e " + rank.getNerds().stream()
				.sorted(Comparator.comparing(Nerd::getName, String.CASE_INSENSITIVE_ORDER))
				.map(Nerd::getName)
				.filter(name -> !name.equals("KodaBear"))
				.collect(Collectors.joining("&f, &e"))));
		line();
		send("&3View online staff with &c/onlinestaff&3.");
		send("&3If you need to request a staff member's &ehelp&3, please use &c/ticket &c<message>");
		line();
	}

}