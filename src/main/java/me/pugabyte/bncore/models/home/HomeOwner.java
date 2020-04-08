package me.pugabyte.bncore.models.home;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.features.homes.HomesFeature.maxHomes;

@Data
@Builder
@Entity("homes")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class HomeOwner extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<Home> homes = new ArrayList<>();
	private Set<UUID> fullAccessList = new HashSet<>();
	private boolean autoLock;

	public List<String> getNames() {
		return getNames(null);
	}

	public List<String> getNames(String filter) {
		if (homes == null)
			return new ArrayList<>();
		return homes.stream()
				.map(Home::getName)
				.filter(name -> filter == null || name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	public Optional<Home> getHome(String name) {
		return homes.stream()
				.filter(home -> home.getName().equalsIgnoreCase(name))
				.findFirst();
	}

	@ToString.Include
	public int getMaxHomes() {
		for (int i = maxHomes; i > 0; i--)
			if (BNCore.getPex().playerHas(null, getOfflinePlayer(), "homes.limit." + i))
				return i;

		return 0;
	}

	public void allowAll(OfflinePlayer player) {
		fullAccessList.add(player.getUniqueId());
	}

	public void removeAll(OfflinePlayer player) {
		fullAccessList.remove(player.getUniqueId());
		homes.forEach(home -> home.remove(player));
	}

	public boolean hasGivenAccessTo(OfflinePlayer player) {
		return fullAccessList.contains(player.getUniqueId());
	}

	public void add(Home home) {
		getHome(home.getName()).ifPresent(this::delete);
		homes.add(home);
	}

	public void delete(Home home) {
		homes.remove(home);
	}

}
