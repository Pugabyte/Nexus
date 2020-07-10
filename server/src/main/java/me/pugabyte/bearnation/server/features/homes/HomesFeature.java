package me.pugabyte.bearnation.server.features.homes;

import me.pugabyte.bearnation.api.utils.StringUtils;

public class HomesFeature {
	public final static String PREFIX = StringUtils.getPrefix("Homes");
	public final static int maxHomes = 100;

	public HomesFeature() {
		new HomeListener();
	}

}
