package me.pugabyte.bearnation.server.features.warps;

import me.pugabyte.bearnation.api.models.setting.Setting;
import me.pugabyte.bearnation.api.models.setting.SettingService;

public enum WarpMenu {
	MAIN(4),
	SURVIVAL(5),
	LEGACY(5),
	MINIGAMES(5),
	CREATIVE(),
	SKYBLOCK(),
	OTHER(6),
	BUILD_CONTESTS(3);

	private int size = 0;

	WarpMenu() {
	}

	WarpMenu(int size) {
		this.size = size;
	}

	public int getSize() {
		if (this == MAIN) {
			SettingService service = new SettingService();
			Setting bc = service.get("buildcontest", "info");
			if (bc != null && bc.getJson().containsKey("active") && (Boolean) bc.getJson().get("active"))
				return size + 2;
		}
		return size;
	}


}
