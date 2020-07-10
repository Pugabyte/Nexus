package me.pugabyte.bearnation.survival.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;

public class TradeSignsCommand extends CustomCommand {

	public TradeSignsCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		line();
		send(json("&eClick here &3to open the wiki on &eTrade Signs").url("https://wiki.bnn.gg/wiki/Economy#Trade_Signs"));
		line();
	}
}
