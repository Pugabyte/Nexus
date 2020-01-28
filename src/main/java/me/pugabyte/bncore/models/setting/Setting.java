package me.pugabyte.bncore.models.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.utils.SerializationUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Setting {
	@NonNull
	private String id;
	@NonNull
	private String type;
	private String value;

	public Setting(Player player, String type, String value) {
		this(player.getUniqueId().toString(), type, value);
	}

	public boolean getBoolean() {
		return Boolean.parseBoolean(value);
	}

	public void setBoolean(boolean value) {
		this.value = String.valueOf(value);
	}

	public Location getLocation() {
		return SerializationUtils.deserializeDatabaseLocation(value);
	}

	public void setLocation(Location location) {
		this.value = SerializationUtils.serializeDatabaseLocation(location);
	}

}
