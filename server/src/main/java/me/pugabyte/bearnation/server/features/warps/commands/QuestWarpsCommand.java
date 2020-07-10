package me.pugabyte.bearnation.server.features.warps.commands;

import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.warps.WarpType;

@Aliases("questwarp")
@Permission("group.staff")
public class QuestWarpsCommand extends _WarpCommand {

	public QuestWarpsCommand(CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.QUEST;
	}

}
