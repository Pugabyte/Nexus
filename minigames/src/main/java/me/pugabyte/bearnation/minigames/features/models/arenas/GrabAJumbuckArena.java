package me.pugabyte.bearnation.minigames.features.models.arenas;

import lombok.Data;
import me.pugabyte.bearnation.api.utils.SerializationUtils;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@SerializableAs("GrabAJumbuckArena")
public class GrabAJumbuckArena extends Arena {
	private Set<Material> sheepSpawnBlocks = new HashSet<>();

	public GrabAJumbuckArena(Map<String, Object> map) {
		super(map);
		this.sheepSpawnBlocks = SerializationUtils.YML.deserializeMaterialSet((List<String>) map.getOrDefault("sheepSpawnBlocks", new ArrayList<>()));
	}

	@Override
	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) super.serialize();
		map.put("sheepSpawnBlocks", SerializationUtils.YML.serializeMaterialSet(sheepSpawnBlocks));

		return map;
	}


}
