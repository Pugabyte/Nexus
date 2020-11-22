package me.pugabyte.nexus.models.nerd;

import de.tr7zw.nbtapi.NBTFile;
import de.tr7zw.nbtapi.NBTList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Nerd {
	private String uuid;
	private String name;
	private String preferredName;
	private LocalDate birthday;
	private LocalDateTime firstJoin;
	private LocalDateTime lastJoin;
	private LocalDateTime lastQuit;
	private LocalDate promotionDate;
	private String about;
	private boolean meetMeVideo;

	public Nerd(String name) {
		this(Utils.getPlayer(name));
	}

	public Nerd(UUID uuid) {
		this(Utils.getPlayer(uuid));
	}

	public Nerd(OfflinePlayer player) {
		fromPlayer(player);
	}

	public void send(String message) {
		getPlayer().sendMessage(colorize(message));
	}

	public void send(int delay, String message) {
		Tasks.wait(delay, () -> send(message));
	}

	protected void send(JsonBuilder builder) {
		builder.send(getPlayer());
	}

	public void fromPlayer(OfflinePlayer player) {
		uuid = player.getUniqueId().toString();
		name = player.getName();
		firstJoin = Utils.epochMilli(player.getFirstPlayed());
	}

	public OfflinePlayer getOfflinePlayer() {
		return Utils.getPlayer(uuid);
	}

	public Player getPlayer() {
		return Utils.getPlayer(uuid).getPlayer();
	}

	public long getTimeOffline(ChronoUnit unit) {
		if (getLastQuit() == null || getOfflinePlayer().isOnline())
			return 0;
		return getLastQuit().until(LocalDateTime.now(), unit);
	}

	public Rank getRank() {
		return Rank.getHighestRank(getOfflinePlayer());
	}

	public String getRankFormat() {
		return getRank().getColor() + getName();
	}

	private static final String CHECKMARK = "&a✔";

	public String getChatFormat() {
		if ("KodaBear".equals(name))
			return "&5KodaBear";

		Rank rank = getRank();
		String prefix = null;
		Setting checkmarkSetting = new SettingService().get(getOfflinePlayer(), "checkmark");
		Setting prefixSetting = new SettingService().get(getOfflinePlayer(), "prefix");

		if (prefixSetting != null)
			prefix = prefixSetting.getValue();

		if (isNullOrEmpty(prefix))
			prefix = rank.getPrefix();

		if (!isNullOrEmpty(prefix))
			prefix = "&8&l[&f" + prefix + "&8&l]";

		if (Nexus.getPerms().playerHas(null, getOfflinePlayer(), "donated") && checkmarkSetting != null && checkmarkSetting.getBoolean())
			prefix = CHECKMARK + " " + prefix;
		return colorize((prefix.trim() + " " + (rank.getColor() + getName()).trim())).trim();
	}

	public boolean isVanished() {
		return Utils.isVanished(getPlayer());
	}

	@SneakyThrows
	public NBTFile getDataFile() {
		File file = Paths.get(Bukkit.getServer().getWorlds().get(0).getName() + "/playerdata/" + getUuid() + ".dat").toFile();
		if (file.exists())
			return new NBTFile(file);
		return null;
	}

	public World getDimension() {
		NBTFile dataFile = getDataFile();
		if (dataFile == null)
			return null;

		String dimension = dataFile.getString("Dimension").replace("minecraft:", "");
		if (isNullOrEmpty(dimension))
			dimension = dataFile.getString("SpawnWorld");

		return Bukkit.getWorld(dimension);
	}

	public Location getLocation() {
		if (getOfflinePlayer().isOnline())
			return getPlayer().getPlayer().getLocation();

		try {
			NBTFile file = getDataFile();
			if (file == null)
				throw new InvalidInputException("Data file does not exist");

			World world = getDimension();
			if (world == null)
				throw new InvalidInputException("Player is not in a valid world (" + world + ")");

			NBTList<Double> pos = file.getDoubleList("Pos");
			NBTList<Float> rotation = file.getFloatList("Rotation");

			return new Location(world, pos.get(0), pos.get(1), pos.get(2), rotation.get(0), rotation.get(1));
		} catch (Exception ex) {
			throw new InvalidInputException("Could not get location of offline player: " + ex.getMessage());
		}
	}

	@Data
	public static class StaffMember extends PlayerOwnedObject {
		@NonNull
		private UUID uuid;
	}

}