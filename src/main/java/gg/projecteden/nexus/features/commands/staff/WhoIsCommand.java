package gg.projecteden.nexus.features.commands.staff;

import com.destroystokyo.paper.ClientOption;
import com.destroystokyo.paper.ClientOption.ChatVisibility;
import gg.projecteden.nexus.features.resourcepack.ResourcePackCommand;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.godmode.Godmode;
import gg.projecteden.nexus.models.godmode.GodmodeService;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.utils.TimeUtils.Timespan;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;
import lombok.NonNull;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Set;

import static gg.projecteden.nexus.utils.StringUtils.getLocationString;
import static gg.projecteden.utils.TimeUtils.shortDateTimeFormat;

@Aliases({"whotf", "whothefuck"})
@Permission("group.staff")
public class WhoIsCommand extends CustomCommand {

	public WhoIsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Async
	@Path("<player>")
	void run(Nerd nerd) {
		line();
		line();
		send("&3Who the fuck is &6&l" + nerd.getNickname() + "&3?");

		HoursService hoursService = new HoursService();
		GeoIPService geoIpService = new GeoIPService();

		Punishments punishments = Punishments.of(nerd);
		boolean history = punishments.hasHistory();
		JsonBuilder alts = punishments.getAltsMessage();

		Hours hours = hoursService.get(nerd);
		String rank = nerd.getRank().getColoredName();
		String firstJoin = shortDateTimeFormat(nerd.getFirstJoin());
		String lastJoinQuitLabel = null;
		String lastJoinQuitDate = null;
		String lastJoinQuitDiff = null;
		OfflinePlayer offlinePlayer = nerd.getOfflinePlayer();
		Player player = offlinePlayer.getPlayer();

		if (offlinePlayer.isOnline()) {
			if (nerd.getLastQuit() != null) {
				lastJoinQuitLabel = "Last Quit";
				lastJoinQuitDate = shortDateTimeFormat(nerd.getLastQuit());
				lastJoinQuitDiff = Timespan.of(nerd.getLastQuit()).format();
			}
		} else {
			lastJoinQuitLabel = "Last Join";
			lastJoinQuitDate = shortDateTimeFormat(nerd.getLastQuit());
			lastJoinQuitDiff = Timespan.of(nerd.getLastJoin()).format();
		}
		Set<String> pastNames = nerd.getPastNames();
		Godmode godmode = new GodmodeService().get(nerd);

		JsonBuilder json = json();

		if (nerd.hasNickname())
			json.newline().next("&3Real Name: &e" + nerd.getName()).group();

		json.newline().next("&3Rank: &e" + rank).group();
		json.newline().next("&3First Join: &e" + firstJoin).group();

		if (lastJoinQuitDate != null)
			json.newline().next("&3" + lastJoinQuitLabel + ": &e" + lastJoinQuitDiff + " ago").hover("&e" + lastJoinQuitDate).group();

		if (hours.getTotal() > 0)
			json.newline().next("&3Hours: &e" + TimespanBuilder.of(hours.getTotal()).noneDisplay(true).format()).group();

		if (history)
			json.newline().next("&3History: &e" + punishments.getPunishments().size()).command("/history " + nerd.getName()).hover("&eClick to view history").group();

		if (alts != null)
			json.newline().next("&3Alts: &e").next(alts).group();

		if (!pastNames.isEmpty())
			json.newline().next("&3Past Names: &e" + String.join("&3, &e", pastNames)).group();

		try {
			GeoIP geoIp = geoIpService.get(nerd);
			if (!isNullOrEmpty(geoIp.getIp()))
				json.newline().next("&3GeoIP: &e" + geoIp.getFriendlyLocationString()).hover("&e" + geoIp.getIp()).suggest(geoIp.getIp()).group();
		} catch (InvalidInputException ex) {
			json.newline().next("&3GeoIP: &c" + ex.getMessage()).group();
		}

		try {
			json.newline().next("&3Location: &e" + getLocationString(nerd.getLocation())).hover("&eClick to TP").command("/tp " + offlinePlayer.getName()).group();
		} catch (InvalidInputException ex) {
			json.newline().next("&3Location: &c" + ex.getMessage()).group();
		}

		json.newline().next("&3Balances:");
		for (ShopGroup shopGroup : ShopGroup.values())
			if (new BankerService().getBalance(offlinePlayer, shopGroup) != 500)
				json.newline().next("  &3" + camelCase(shopGroup) + ": &e" + new BankerService().getBalanceFormatted(offlinePlayer, shopGroup)).group();

		if (player != null) {
			json.newline().next("&3Minecraft Version: &e" + PlayerUtils.getPlayerVersion(player));

			json.newline().next("&3Client Brand Name: &e" + player.getClientBrandName()).group();

			json.newline().next("&3Gamemode: &e" + camelCase(player.getGameMode())).group();

			json.newline().next("&3God mode: &e" + godmode.isEnabledRaw()).group();

			json.newline().next("&3Fly mode: &e" + player.getAllowFlight() + " &3(" + (player.isFlying() ? "flying" : "not flying") + ")").group();

			json.newline().next("&3RP status: &e" + ResourcePackCommand.statusOf(player)).group();

			final ChatVisibility chatVisibility = player.getClientOption(ClientOption.CHAT_VISIBILITY);
			if (chatVisibility != ChatVisibility.FULL)
				json.newline().next("&3Chat Visibility: &e" + camelCase(chatVisibility));
		}

		json.newline().next("&3OP: &e" + offlinePlayer.isOp()).group();

		send(json);
	}

}
