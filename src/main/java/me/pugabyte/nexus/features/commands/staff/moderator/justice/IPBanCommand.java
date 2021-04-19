package me.pugabyte.nexus.features.commands.staff.moderator.justice;

import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.Switch;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.PunishmentType;
import me.pugabyte.nexus.models.punishments.Punishments;

import java.util.List;

@Permission("group.moderator")
//@Aliases("banip")
public class IPBanCommand extends _PunishmentCommand {

	public IPBanCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [time/reason...] [--now]")
	void run(@Arg(type = Punishments.class) List<Punishments> players, String input, @Switch boolean now) {
		punish(players, input, now);
	}

	@Override
	protected PunishmentType getType() {
		return PunishmentType.IP_BAN;
	}

}
