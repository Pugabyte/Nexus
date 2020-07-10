package me.pugabyte.bearnation.server.features.wiki;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

@Aliases("minecraftwiki")
public class MCWikiCommand extends CustomCommand {

	MCWikiCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		send("&eVisit the minecraft wiki at &3https://minecraft.gamepedia.com/");
		send("&eOr use &c/mcwiki search <query> &eto search the wiki from ingame.");
	}

	@Path("search <query...>")
	void search(String search) {
		Wiki.search(sender(), search.split(" "), "MCWiki");
	}
}
