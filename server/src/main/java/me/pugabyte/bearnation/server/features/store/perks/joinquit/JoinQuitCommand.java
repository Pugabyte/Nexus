package me.pugabyte.bearnation.server.features.store.perks.joinquit;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.StringUtils;

@Aliases("jq")
public class JoinQuitCommand extends CustomCommand {

	public JoinQuitCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("JQ");
	}

	@Path("reload")
	@Permission("group.seniorstaff")
	void reload() {
		JoinQuit.reloadConfig();
		send(PREFIX + "Successfully loaded " + JoinQuit.getJoinMessages().size() + " join and " + JoinQuit.getQuitMessages().size() + " quit messages");
	}

}
