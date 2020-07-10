package me.pugabyte.bearnation.survival.features.shops;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.features.shops.providers.BrowseMarketProvider;

public class MarketCommand extends CustomCommand {

	public MarketCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		new BrowseMarketProvider(null).open(player());
	}

}
