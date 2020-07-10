package me.pugabyte.bearnation.server.models.mutemenu;

import me.pugabyte.bearnation.server.models.MySQLService;

public class MuteMenuService extends MySQLService {

	public MuteMenu get(String uuid) {
		MuteMenu muteMenu = database.where("uuid = ?", uuid).first(MuteMenu.class);
		if (muteMenu.getUuid() != null)
			return muteMenu;
		muteMenu = new MuteMenu(uuid);
		save(muteMenu);
		return muteMenu;
	}

}
