package gg.projecteden.nexus.features.commands.staff.operator;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.geoip.GeoIP;
import gg.projecteden.nexus.models.geoip.GeoIP.Distance;
import gg.projecteden.nexus.models.geoip.GeoIPService;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.TimeUtils.Time;
import lombok.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

@Permission("group.seniorstaff")
public class IrlNearCommand extends CustomCommand {

	public IrlNearCommand(@NonNull CommandEvent event) {
		super(event);
		PREFIX = StringUtils.getPrefix("GeoIP");
	}

	@Async
	@Path("[player] [page]")
	void run(@Arg("self") GeoIP player, @Arg("1") int page) {
		Map<UUID, Distance> near = new HashMap<>() {{
			for (GeoIP geoip : new GeoIPService().getAll())
				if (new HoursService().get(geoip).getTotal() > Time.MINUTE.x(30) / 20)
					put(geoip.getUuid(), new Distance(player, geoip));
		}};

		BiFunction<UUID, String, JsonBuilder> formatter = (uuid, index) -> {
			Distance distance = near.get(uuid);
			String mi = distance.getMilesFormatted();
			String km = distance.getKilometersFormatted();
			return json("&3" + index + " &e" + Nickname.of(uuid) + " &7- " + mi + "mi / " + km + "km");
		};

		paginate(Utils.sortByValue(near).keySet(), formatter, "/irlnear " + player.getNickname(), page);
	}

}
