package me.pugabyte.bncore.models.homes;

import dev.morphia.annotations.Converters;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Converters({LocationConverter.class, UUIDConverter.class})
public class Home extends PlayerOwnedObject {
	@NonNull
	private UUID uuid;
	@NonNull
	private String name;
	@NonNull
	private Location location;
	private boolean locked;
	// TODO: Needs converter
	private ItemStack item;
	private Set<UUID> accessList = new HashSet<>();

	public HomeOwner getOwner() {
		return new HomeService().get(uuid);
	}

	public void teleport(Player player) {
		if (hasAccess(player))
			player.teleport(location.clone().add(0, .5, 0));
		else
			player.sendMessage(PREFIX + colorize("&cYou don't have acces"));
	}

	public boolean hasAccess(Player player) {
		return player.getUniqueId().equals(getOfflinePlayer().getUniqueId())
				|| getOwner().hasGivenAccessTo(player)
				|| accessList.contains(player.getUniqueId());
	}

	public void allow(OfflinePlayer player) {
		accessList.add(player.getUniqueId());
	}

	public void remove(OfflinePlayer player) {
		accessList.remove(player.getUniqueId());
	}

}
