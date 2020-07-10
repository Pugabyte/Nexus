package me.pugabyte.bearnation.server.features.store.perks;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.setting.Setting;
import me.pugabyte.bearnation.api.models.setting.SettingService;
import me.pugabyte.bearnation.features.chat.Emotes;
import org.bukkit.OfflinePlayer;

import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripFormat;

public class PrefixCommand extends CustomCommand {
	SettingService service = new SettingService();
	Setting checkmark = null;
	Setting prefix = null;

	public PrefixCommand(CommandEvent event) {
		super(event);
		if (isPlayer()) {
			checkmark = service.get(player(), "checkmark");
			prefix = service.get(player(), "prefix");
		}
	}

	@Path("checkmark")
	@Permission("donated")
	void checkmark() {
		checkmark.setBoolean(!checkmark.getBoolean());
		send(PREFIX + "Check mark " + (checkmark.getBoolean() ? "enabled" : "disabled"));
		service.save(checkmark);
	}

	@Path("reset")
	@Permission("set.my.prefix")
	void reset() {
		service.delete(prefix);
		send(PREFIX + "Reset prefix");
	}

	@Path("expire <player>")
	void expire(OfflinePlayer player) {
		console();
		prefix = service.get(player, "prefix");
		service.delete(prefix);
		send(PREFIX + "Reset prefix");
	}

	@Path("<prefix...>")
	@Permission("set.my.prefix")
	void prefix(String value) {
		if (player().hasPermission("emoticons.use"))
			value = Emotes.process(value);

		if (stripColor(value).length() > 10)
			error("Your prefix cannot be more than 10 characters");

		value = stripFormat(value);

		prefix.setValue(value);
		service.save(prefix);
		send(PREFIX + "Your prefix has been set to &8&l[&f" + value + "&8&l]");
	}

}
