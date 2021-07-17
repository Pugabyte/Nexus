package me.pugabyte.nexus.features.mobheads.common;

import me.pugabyte.nexus.features.mobheads.MobHeadType;
import org.bukkit.inventory.ItemStack;

import static eden.utils.StringUtils.camelCase;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

public interface MobHeadVariant extends MobHead {

	@Override
	default MobHeadType getType() {
		return MobHeadType.of(getEntityType());
	}

	@Override
	default MobHeadVariant getVariant() {
		return this;
	}

	ItemStack getItemStack();

	default ItemStack getSkull() {
		ItemStack skull = getItemStack();
		if (isNullOrAir(skull))
			return MobHeadType.of(getEntityType()).getSkull();
		return skull;
	}

	void setItemStack(ItemStack itemStack);

	default String getDisplayName() {
		return "&e" + camelCase((Enum<?>) this) + " " + camelCase(getEntityType()) + " Head";
	}

}
