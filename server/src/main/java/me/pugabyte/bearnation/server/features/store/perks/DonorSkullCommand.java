package me.pugabyte.bearnation.server.features.store.perks;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Cooldown.Part;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.ItemBuilder;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.api.utils.Utils;
import org.bukkit.Material;

@Cooldown(@Part(Time.DAY))
@Permission("essentials.skull")
public class DonorSkullCommand extends CustomCommand {

	public DonorSkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		Utils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player()).build());
	}

}
