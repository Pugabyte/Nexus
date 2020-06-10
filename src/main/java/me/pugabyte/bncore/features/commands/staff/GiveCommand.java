package me.pugabyte.bncore.features.commands.staff;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Aliases("i")
@Permission("essentials.give")
public class GiveCommand extends CustomCommand {

	public GiveCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> <type> [amount]")
	void run(Player player, Material material, @Arg("1") int amount) {
		Utils.giveItem(player, material, amount);
	}

}