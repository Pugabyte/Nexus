package me.pugabyte.bearnation.survival.features.atp;

import fr.minuskube.inv.SmartInventory;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.features.atp.ATPMenuProvider.ATPGroup;
import org.bukkit.entity.Player;

public class ATPMenu {

	public void open(Player player, ATPGroup group) {
		SmartInventory INV = SmartInventory.builder()
				.size(5, 9)
				.title(StringUtils.colorize("&3Animal Teleport Pens"))
				.provider(new ATPMenuProvider(group))
				.build();
		INV.open(player);
	}

}
