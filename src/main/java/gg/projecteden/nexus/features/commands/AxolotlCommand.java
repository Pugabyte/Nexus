package gg.projecteden.nexus.features.commands;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerBucketEntityEvent;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Permission("group.seniorstaff")
public class AxolotlCommand extends CustomCommand implements Listener {

	public AxolotlCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("hat <variant>")
	void hat(Axolotl.Variant variant) {
		giveItem(new ItemBuilder(Material.STONE_BUTTON)
			.customModelData(100 + variant.ordinal())
			.soulbound()
			.untrashable()
			.untradeable()
			.unplaceable()
			.build());
	}

	@Path("bucket <variant> [amount]")
	void variant(Axolotl.Variant variant, @Arg("1") int amount) {
		for (int i = 0; i < amount; i++)
			PlayerUtils.giveItem(player(), new ItemBuilder(Material.AXOLOTL_BUCKET).axolotl(variant).build());
	}

	@EventHandler
	public void onPlayerBucketEntity(PlayerBucketEntityEvent event) {
		final Entity entity = event.getEntity();
		if (!(entity instanceof Axolotl axolotl))
			return;

		final ItemStack bucket = event.getEntityBucket();
		new NBTItem(bucket, true).setInteger(CustomModel.NBT_KEY, axolotl.getVariant().ordinal());
	}

	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		if (event.getTarget() == null)
			return;

		if (event.getEntity().getType() != EntityType.AXOLOTL)
			return;

		if (event.getTarget().getType() != EntityType.TROPICAL_FISH)
			return;

		event.setCancelled(true);
	}

}
