package me.pugabyte.bearnation.server.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Permission("essentials.give")
public class GiveCommand extends CustomCommand {

	public GiveCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <type> [amount] [nbt...]")
	void run(Player player, Material material, @Arg("64") int amount, @Arg(permission = "group.staff") String nbt) {
		Utils.giveItem(player, material, amount, nbt);
	}

}
