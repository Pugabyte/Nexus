package me.pugabyte.bearnation.minigames.features.models;

import lombok.NoArgsConstructor;
import lombok.ToString;
import me.pugabyte.bearnation.api.utils.WorldEditUtils;
import me.pugabyte.bearnation.api.utils.WorldGuardUtils;

@NoArgsConstructor
public class MatchData {
	@ToString.Exclude
	private Match match;
	protected WorldGuardUtils WGUtils;
	protected WorldEditUtils WEUtils;

	public MatchData(Match match) {
		this.match = match;
		WGUtils = match.getArena().getWGUtils();
		WEUtils = match.getArena().getWEUtils();
	}

	public Match getMatch() {
		return match;
	}

	public void setMatch(Match match) {
		this.match = match;
	}

}
