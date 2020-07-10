package me.pugabyte.bearnation.server.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.ItemBuilder;

@Aliases("nameitem")
@Permission("itemname.use")
public class ItemNameCommand extends CustomCommand {

	public ItemNameCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("(null|none|reset)")
	void name() {
		name(null);
	}

	@Path("<name...>")
	void name(String name) {
		ItemBuilder.setName(getToolRequired(), name);
	}

}
