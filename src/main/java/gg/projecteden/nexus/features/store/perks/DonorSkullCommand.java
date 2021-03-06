package gg.projecteden.nexus.features.store.perks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown.Part;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NonNull;
import org.bukkit.Material;

import static gg.projecteden.nexus.features.store.perks.DonorSkullCommand.PERMISSION;

@Cooldown(value = @Part(Time.DAY), bypass = "group.admin")
@Permission(PERMISSION)
public class DonorSkullCommand extends CustomCommand {
	public static final String PERMISSION = "essentials.skull";

	public DonorSkullCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	void run() {
		PlayerUtils.giveItem(player(), new ItemBuilder(Material.PLAYER_HEAD).skullOwner(player()).build());
	}

}
