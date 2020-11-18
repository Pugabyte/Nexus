package me.pugabyte.bncore.features.kits;

import lombok.Data;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.utils.ItemUtils;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@SerializableAs("Kit")
public class Kit implements ConfigurationSerializable {

	public int id;
	String name = "Kit";
	ItemStack[] items = new ItemStack[0];
	int delay = 0;

	public Kit(Map<String, Object> map) {
		this.name = (String) map.getOrDefault("name", name);
		this.items = Arrays.stream(SerializationUtils.YML.deserializeItems((Map<String, Object>) map.getOrDefault("items", items)))
				.filter(itemStack -> !ItemUtils.isNullOrAir(itemStack)).collect(Collectors.toList()).toArray(new ItemStack[0]);
		this.delay = (int) map.getOrDefault("delay", delay);
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("name", name);
			put("items", SerializationUtils.YML.serializeItems(items));
			put("delay", delay);
		}};
	}

}
