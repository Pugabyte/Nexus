package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.geoip.GeoIP;
import me.pugabyte.nexus.models.geoip.GeoIPService;
import me.pugabyte.nexus.models.setting.Setting;
import me.pugabyte.nexus.models.setting.SettingService;
import me.pugabyte.nexus.utils.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Aliases("timefor")
public class CurrentTimeCommand extends CustomCommand {
	GeoIPService geoIpService = new GeoIPService();
	SettingService settingService = new SettingService();

	public CurrentTimeCommand(CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("Timezones");
	}

	@Path("format <12/24>")
	void format(int format) {
		if (format != 12 && format != 24)
			send(json()
					.line()
					.next(" &eClick &3 the time format you prefer:  ")
					.next("&e12 hour")
					.command("/currenttime format 12")
					.hover("&eClick &3to set the display format to &e12 hour")
					.group()
					.next("  &3||  &3")
					.next("&e24 hour")
					.command("/currenttime format 24")
					.hover("&eClick &3to set the display format to &e24 hour")
					.group());
		else {
			Setting setting = settingService.get(player(), "timezoneFormat");
			setting.setValue(String.valueOf(format));
			settingService.save(setting);
			send(PREFIX + "Time format set to &e" + format + " hour");
		}
	}

	@Path("update")
	void update() {
		send(PREFIX + "Updating your timezone information...");
		GeoIP geoIp = geoIpService.request(player());
		if (geoIp == null || geoIp.getIp() == null)
			error("There was an error while updating your timezone. Please try again later.");

		geoIpService.save(geoIp);
		send(PREFIX + "Updated your timezone to &3" + geoIp.getTimezone());
	}

	@Path("<player>")
	void timeFor(GeoIP geoIp) {
		if (geoIp == null || geoIp.getIp() == null)
			error("That player's timezone is not set.");

		DateFormat format = getDateFormat();
		format.setTimeZone(TimeZone.getTimeZone(geoIp.getTimezone().getId()));
		send(PREFIX + "The current time for &e" + geoIp.getOfflinePlayer().getName() + " &3is &e" + format.format(new Date()));
	}

	@NotNull
	private DateFormat getDateFormat() {
		Setting setting = settingService.get(player(), "timezoneFormat");
		DateFormat format = new SimpleDateFormat("h:mm aa");
		if (setting.getValue() != null && setting.getValue().equalsIgnoreCase("24"))
			format = new SimpleDateFormat("HH:mm");
		return format;
	}

	@Path()
	void help() {
		send(PREFIX + "This command shows you what time it is for other players.");
		send(json("&eClick here &3to change the time format.")
				.hover("&3The valid formats are &e12 &3and &e24 &3hours. It defaults to &e12 &3hours.")
				.command("/currenttime format"));
		line();
		send("&3Usage: &c/currenttime <player>");
	}

}