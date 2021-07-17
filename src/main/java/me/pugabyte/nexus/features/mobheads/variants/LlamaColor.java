package me.pugabyte.nexus.features.mobheads.variants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Llama.Color;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LlamaColor implements MobHeadVariant {
	GRAY(Color.GRAY),
	WHITE(Color.WHITE),
	BROWN(Color.BROWN),
	CREAMY(Color.CREAMY),
	;

	private final Color type;
	@Setter
	private ItemStack itemStack;

	@Override
	public EntityType getEntityType() {
		return EntityType.LLAMA;
	}

	public static LlamaColor of(Llama llama) {
		return Arrays.stream(values()).filter(entry -> llama.getColor() == entry.getType()).findFirst().orElse(null);
	}
}
