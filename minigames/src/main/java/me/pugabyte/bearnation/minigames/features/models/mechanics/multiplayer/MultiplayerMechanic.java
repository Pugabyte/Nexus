package me.pugabyte.bearnation.minigames.features.models.mechanics.multiplayer;

import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bearnation.minigames.features.models.mechanics.Mechanic;

public abstract class MultiplayerMechanic extends Mechanic {

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		if (event.getMatch().isEnded())
			return;

		Minigamer victim = event.getMinigamer();
		if (victim.isRespawning()) return;

		victim.clearState();
		if (victim.getLives() != 0) {
			victim.died();
			if (victim.getLives() == 0) {
				victim.setAlive(false);
				if (victim.getMatch().getArena().getSpectateLocation() == null)
					victim.quit();
				else
					victim.toSpectate();
			} else {
				victim.respawn();
			}
		} else {
			victim.respawn();
		}

		super.onDeath(event);
	}

	@Override
	public void processJoin(Minigamer minigamer) {
		Match match = minigamer.getMatch();
		if (match.isStarted()) {
			balance(minigamer);
			match.teleportIn(minigamer);
		} else {
			match.getArena().getLobby().join(minigamer);
		}
	}

}
