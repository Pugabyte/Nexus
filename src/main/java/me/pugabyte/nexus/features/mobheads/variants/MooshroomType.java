package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MooshroomType implements MobHeadVariant {
	RED(Variant.RED),
	BROWN(Variant.BROWN),
	;

	private final Variant type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.MUSHROOM_COW;
	}

	public static MooshroomType of(MushroomCow mushroomCow) {
		return Arrays.stream(values()).filter(entry -> mushroomCow.getVariant() == entry.getType()).findFirst().orElse(null);
	}
}
