package me.pugabyte.bncore.features.minigames.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.Accessors;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.MechanicType;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SerializableAs("Arena")
public class Arena implements ConfigurationSerializable {
	@NonNull
	private int id;
	@NonNull
	private String name;
	@NonNull
	private String displayName;
	@NonNull
	private MechanicType mechanicType;
	@NonNull
	private List<Team> teams;
	@NonNull
	private Lobby lobby;
	private Location respawnLocation;
	private Location spectateLocation;
	private int seconds;
	private int minPlayers;
	private int maxPlayers;
	private int winningScore;
	private int minWinningScore;
	private int maxWinningScore;
	private Set<Material> blockList;
	@Accessors(fluent = true)
	private boolean isWhitelist = true;
	@Accessors(fluent = true)
	private boolean canJoinLate;
	@Accessors(fluent = true)
	private boolean hasScoreboard = true;

	public Mechanic getMechanic() {
		return getMechanicType().get();
	}

	@Override
	public Map<String, Object> serialize() {
		return new LinkedHashMap<String, Object>() {{
			put("id", getId());
			put("name", getName());
			put("displayName", getDisplayName());
			put("mechanicType", getMechanicType().name());
			put("teams", getTeams());
			put("lobby", getLobby());
			put("respawnLocation", getRespawnLocation());
			put("spectateLocation", getSpectateLocation());
			put("seconds", getSeconds());
			put("minPlayers", getMinPlayers());
			put("maxPlayers", getMaxPlayers());
			put("winningScore", getWinningScore());
			put("minWinningScore", getMinWinningScore());
			put("maxWinningScore", getMaxWinningScore());
			put("blockList", getBlockList());
			put("isWhitelist", isWhitelist());
			put("canJoinLate", canJoinLate());
			put("hasScoreboard", hasScoreboard());
		}};
	}

	public Arena(Map<String, Object> map) {
		this.id = (int) map.get("id");
		this.name = (String) map.get("name");
		this.displayName = (String) map.get("displayName");
		this.mechanicType = MechanicType.valueOf(((String) map.get("mechanicType")).toUpperCase());
		this.teams = (List<Team>) map.get("teams");
		this.lobby = (Lobby) map.get("lobby");
		this.respawnLocation = (Location) map.get("respawnLocation");
		this.spectateLocation = (Location) map.get("spectateLocation");
		this.seconds = (Integer) map.get("seconds");
		this.minPlayers = (Integer) map.get("minPlayers");
		this.maxPlayers = (Integer) map.get("maxPlayers");
		this.winningScore = (Integer) map.get("winningScore");
		this.minWinningScore = (Integer) map.get("minWinningScore");
		this.maxWinningScore = (Integer) map.get("maxWinningScore");
		this.blockList = (Set<Material>) map.get("blockList");
		this.isWhitelist = (Boolean) map.getOrDefault("isWhitelist", isWhitelist);
		this.canJoinLate = (Boolean) map.getOrDefault("canJoinLate", canJoinLate);
		this.hasScoreboard = (Boolean) map.getOrDefault("hasScoreboard", hasScoreboard);
	}

	public boolean canUseBlock(Material type) {
		if (blockList == null || blockList.size() == 0) return true;

		if (isWhitelist)
			return blockList.contains(type);
		else
			return !blockList.contains(type);
	}

}
