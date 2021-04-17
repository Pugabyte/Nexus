package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;

import static me.pugabyte.nexus.utils.TimeUtils.shortDateFormat;

public class AdminCommand extends CustomCommand {

	public AdminCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void admin() {
		line(5);
		send(Rank.ADMIN.getChatColor() + "Administrator &3is the highest possible rank to achieve on the server. They are in charge of the &eentire &3server and staff, " +
				"and making sure everything is running as it should.");
		line();
		send("&3[+] &eSenior Staff rank");
		send("&3[+] &eHow to achieve&3: &3Promoted from " + Rank.OPERATOR.getChatColor() + "Operator &3by existing Admins");
		send(json("&3[+] &eClick here &3for a list of admins").command("/admin list"));
		line();
		RanksCommand.ranksReturn(player());
	}

	@Async
	@Path("list")
	void list() {
		line();
		send("&3All current " + Rank.ADMIN.getChatColor() + "Admins &3and the date they were promoted:");
		Rank.ADMIN.getNerds().forEach(nerd ->
				send(nerd.getColoredName() + " &7-&e " + shortDateFormat(nerd.getPromotionDate())));
		line();
		RanksCommand.ranksReturn(player());
	}
}
