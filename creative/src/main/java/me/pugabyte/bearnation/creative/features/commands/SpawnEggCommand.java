package me.pugabyte.bearnation.creative.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.bearnation.api.utils.Utils.getSpawnEgg;

@Permission("plots.use")
public class SpawnEggCommand extends CustomCommand {

	public SpawnEggCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<entityType>")
	void give(EntityType entityType) {
		try {
			player().getInventory().setItemInMainHand(new ItemStack(getSpawnEgg(entityType)));
		} catch (Exception ex) {
			error("Could not convert that entity type to a spawn egg");
		}
	}

}
