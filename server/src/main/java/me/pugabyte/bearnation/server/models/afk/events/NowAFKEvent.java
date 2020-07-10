package me.pugabyte.bearnation.server.models.afk.events;

import me.pugabyte.bearnation.server.models.afk.AFKPlayer;

public class NowAFKEvent extends AFKEvent {

	public NowAFKEvent(AFKPlayer player) {
		super(player);
	}

}
