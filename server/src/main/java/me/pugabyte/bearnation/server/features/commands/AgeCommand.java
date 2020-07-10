package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.api.utils.Utils.ServerAge;

import java.time.LocalDate;

public class AgeCommand extends CustomCommand {

	public AgeCommand(CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void player(Nerd nerd) {
		if (arg(1).equalsIgnoreCase("bn") || arg(1).equalsIgnoreCase("bearnation") || arg(1).equalsIgnoreCase("server")) {
			bn();
			return;
		}
		try {
			int year = nerd.getBirthday().until(LocalDate.now()).getYears();
			send(PREFIX + nerd.getName() + " is &e" + year + "&3 years old.");
		} catch (Exception ex) {
			send(PREFIX + "That player does not have a set birthday");
		}

	}

	@Path()
	void bn() {
		ServerAge serverAge = new ServerAge();

		send("&3Bear Nation was born on &eJune 29th, 2015&3, at &e12:52 PM ET");
		send("&3That makes it...");
		line();
		send("&e" + Utils.ServerAge.format(serverAge.getDogYears()) + " &3dog years old");
		send("&e" + Utils.ServerAge.format(serverAge.getYears()) + " &3years old");
		send("&e" + Utils.ServerAge.format(serverAge.getMonths()) + " &3months old");
		send("&e" + Utils.ServerAge.format(serverAge.getWeeks()) + " &3weeks old");
		send("&e" + Utils.ServerAge.format(serverAge.getDays()) + " &3days old");
		send("&e" + Utils.ServerAge.format(serverAge.getHours()) + " &3hours old");
		send("&e" + Utils.ServerAge.format(serverAge.getMinutes()) + " &3minutes old");
		send("&e" + Utils.ServerAge.format(serverAge.getSeconds()) + " &3seconds old");
	}
}
