package me.pugabyte.bearnation.api.framework.exceptions.postconfigured;

import me.pugabyte.bearnation.api.framework.exceptions.BNException;
import org.bukkit.ChatColor;

public class PostConfiguredException extends BNException {

	public PostConfiguredException(String message) {
		super(ChatColor.RED + message);
	}

}
