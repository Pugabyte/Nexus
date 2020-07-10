package me.pugabyte.bearnation.server.models.afk.events;

import me.pugabyte.bearnation.server.models.afk.AFKPlayer;

public class NotAFKEvent extends AFKEvent {

	public NotAFKEvent(AFKPlayer player) {
		super(player);
	}

}
