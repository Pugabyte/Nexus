package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.afk.AFK;
import gg.projecteden.nexus.features.chat.Koda;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.hours.Hours;
import gg.projecteden.nexus.models.hours.HoursService;
import gg.projecteden.nexus.models.hours.HoursService.HoursTopArguments;
import gg.projecteden.nexus.models.hours.HoursService.PageResult;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.SoundUtils.Jingle;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import gg.projecteden.utils.TimeUtils.Timespan;
import gg.projecteden.utils.TimeUtils.Timespan.TimespanBuilder;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Aliases({"playtime", "days", "minutes", "seconds"})
@Description("View a player's play time on the server, excluding AFK time")
public class HoursCommand extends CustomCommand {
	private final HoursService service = new HoursService();

	public HoursCommand(CommandEvent event) {
		super(event);
	}

	private static final int DAY = Time.DAY.get() / 20;

	@Async
	@Path("[player]")
	void player(@Arg("self") Hours hours) {
		boolean isSelf = isSelf(hours);

		send("");
		send(PREFIX + (isSelf ? "Your" : "&e" + hours.getName() + "&3's") + " playtime");
		send("&3Total: &e" + TimespanBuilder.of(hours.getTotal()).noneDisplay(true).format());
		send("&7- &3Today: &e" + TimespanBuilder.of(hours.getDaily()).noneDisplay(true).format());
		send("&7- &3This month: &e" + TimespanBuilder.of(hours.getMonthly()).noneDisplay(true).format());
		send("&7- &3This year: &e" + TimespanBuilder.of(hours.getYearly()).noneDisplay(true).format());

		if (Rank.of(hours) == Rank.GUEST) {

			String who = (isSelf ? "You need" : hours.getName() + " needs") + " ";
			String left = Timespan.of(DAY - hours.getTotal()).format();

			line();
			send("&3" + who + "&e" + left + " more in-game play time &3to achieve &fMember&3.");
		}
	}

	// TODO Update paginate to support database-level pagination
	@Async
	@Description("View the play time leaderboard for any year, month, or day")
	@Path("top [args...]")
	void top2(@Arg("1") HoursTopArguments args) {
		int page = args.getPage();
		List<PageResult> results = service.getPage(args);
		if (results.size() == 0)
			error("&cNo results on page " + page);

		int totalHours = 0;
		for (PageResult result : results)
			totalHours += result.getTotal();

		send("");
		send(PREFIX + "Total: " + Timespan.of(totalHours).format() + (page > 1 ? "&e  |  &3Page " + page : ""));

		BiFunction<PageResult, String, JsonBuilder> formatter = (result, index) ->
				json("&3" + index + " &e" + Nerd.of(result.getUuid()).getColoredName() + " &7- " + Timespan.of(result.getTotal()).format());

		paginate(results, formatter, "/hours top " + args.getInput(), page);
	}


	@ConverterFor(HoursTopArguments.class)
	HoursTopArguments convertToHoursTopArgument(String value) {
		return new HoursTopArguments(value);
	}

	@TabCompleterFor(HoursTopArguments.class)
	List<String> tabCompleteHoursTopArgument(String filter) {
		if (filter.contains(" "))
			return new ArrayList<>();

		Set<String> completions = new HashSet<>();
		LocalDate now = LocalDate.now();
		LocalDate start = LocalDate.of(2020, 6, 1);
		while (!start.isAfter(now)) {
			completions.add(String.valueOf(start.getYear()));
			completions.add(start.getYear() + "-" + String.format("%02d", start.getMonthValue()));
			completions.add(start.getYear() + "-" + String.format("%02d", start.getMonthValue()) + "-" + String.format("%02d", start.getDayOfMonth()));
			start = start.plusDays(1);
		}

		completions.add("daily");
		completions.add("monthly");
		completions.add("yearly");
		completions.remove("2020");

		return completions.stream().filter(completion -> completion.toLowerCase().startsWith(filter)).collect(Collectors.toList());
	}

	private static final int INTERVAL = 5;

	static {
		Tasks.repeatAsync(10, Time.SECOND.x(INTERVAL), () -> {
			for (Player player : PlayerUtils.getOnlinePlayers()) {
				try {
					if (AFK.get(player).isAfk()) continue;

					HoursService service = new HoursService();
					Hours hours = service.get(player.getUniqueId());
					hours.increment(INTERVAL);
					service.update(hours);

					if (Rank.of(player) == Rank.GUEST) {
						if (player.hasPermission("set.my.rank"))
							continue;

						if (hours.getTotal() > DAY) {
							Tasks.sync(() -> {
								PlayerUtils.runCommandAsConsole("lp user " + player.getName() + " parent remove " + Rank.GUEST.name());
								PlayerUtils.runCommandAsConsole("lp user " + player.getName() + " parent add " + Rank.MEMBER.name());
								Koda.say("Congrats on Member rank, " + Nickname.of(player) + "!");
								Jingle.RANKUP.play(player);
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&e&lCongratulations! &3You have been promoted to &fMember&3 for " +
										"playing for &e24 hours &3in-game. You are now eligible for &c/trusted&3.");
								PlayerUtils.send(player, "");
								PlayerUtils.send(player, "&6&lThank you for flying Project Eden!");
							});
						}
					}
				} catch (Exception ex) {
					Nexus.warn("Error in Hours scheduler: " + ex.getMessage());
				}
			}
		});
	}
}


