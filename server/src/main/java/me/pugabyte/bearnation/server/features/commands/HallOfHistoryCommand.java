package me.pugabyte.bearnation.server.features.commands;

import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Async;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.models.nerd.NerdService;
import me.pugabyte.bearnation.api.models.nerd.Rank;
import me.pugabyte.bearnation.api.utils.JsonBuilder;
import me.pugabyte.bearnation.api.utils.MenuUtils.ConfirmationMenu;
import me.pugabyte.bearnation.server.models.hallofhistory.HallOfHistory;
import me.pugabyte.bearnation.server.models.hallofhistory.HallOfHistory.RankHistory;
import me.pugabyte.bearnation.server.models.hallofhistory.HallOfHistoryService;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.bearnation.api.utils.StringUtils.dateFormat;
import static me.pugabyte.bearnation.api.utils.StringUtils.shortDateFormat;
import static me.pugabyte.bearnation.api.utils.StringUtils.stripColor;

@Aliases("hoh")
public class HallOfHistoryCommand extends CustomCommand {
	HallOfHistoryService service = new HallOfHistoryService(getPlugin());

	public HallOfHistoryCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void warp() {
		runCommand("warp hallofhistory");
	}

	@Path("clearCache")
	@Permission("group.seniorstaff")
	void clearCache() {
		service.clearCache();
		send(PREFIX + "Successfully cleared cache");
	}

	@Async
	@Path("view <player>")
	void view(OfflinePlayer target) {
		line(4);
		send("&e&l" + target.getName());
		line();
		HallOfHistory hallOfHistory = service.get(target.getUniqueId());
		for (RankHistory rankHistory : hallOfHistory.getRankHistory()) {
			JsonBuilder builder = new JsonBuilder();
			builder.next("  " + (rankHistory.isCurrent() ? "&2Current" : "&cFormer") + " " + rankHistory.getRank().getChatColor() + rankHistory.getRank().plain());
			if (isPlayer() && player().hasPermission("hoh.edit"))
				builder.next("  &c[x]").command("/hoh removerank " + target.getName() + " " + getRankCommandArgs(rankHistory));

			send(builder);
			send("    &ePromotion Date: &3" + shortDateFormat(rankHistory.getPromotionDate()));
			if (rankHistory.getResignationDate() != null)
				send("    &eResignation Date: &3" + shortDateFormat(rankHistory.getResignationDate()));
		}

		line();
		Nerd nerd = new NerdService(getPlugin()).get(target.getUniqueId());
		if (!isNullOrEmpty(nerd.getAbout()))
			send("  &eAbout me: &3" + nerd.getAbout());
		if (nerd.isMeetMeVideo()) {
			line();
			String url = "https://bnn.gg/meet/" + nerd.getName().toLowerCase();
			send(json("  &eMeet Me!&c " + url).url(url));
		}
	}

	@Permission("hoh.edit")
	@Path("create <player>")
	void create(String player) {
		runCommand("blockcenter");
		tasks().wait(5, () -> runCommand("npc create " + player));
	}

	@Async
	@Permission("hoh.edit")
	@Path("addRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	void addRank(OfflinePlayer target, String when, Rank rank, LocalDate promotion, LocalDate resignation) {
		boolean current = "current".equalsIgnoreCase(when);

		if (!current && resignation == null)
			error("Resignation date was not provided");

		HallOfHistory history = service.get(target);
		history.getRankHistory().add(new RankHistory(rank, current, promotion, resignation));
		service.save(history);
		send(PREFIX + "Successfully saved rank data for &e" + target.getName());
	}

	@Async
	@Permission("hoh.edit")
	@Path("removeRank <player> <current|former> <rank> <promotionDate> [resignationDate]")
	void removeRankConfirm(OfflinePlayer player, String when, Rank rank, LocalDate promotion, LocalDate resignation) {
		boolean current = "current".equalsIgnoreCase(when);

		HallOfHistory history = service.get(player.getUniqueId());
		ConfirmationMenu.builder()
				.title("Remove rank from " + player.getName() + "?")
				.onConfirm((item) -> {
					for (RankHistory rankHistory : new ArrayList<>(history.getRankHistory())) {
						if (!new RankHistory(rank, current, promotion, resignation).equals(rankHistory)) continue;

						history.getRankHistory().remove(rankHistory);
						service.save(history);
						send(PREFIX + "Removed the rank from &e" + player.getName());
						send(json(PREFIX + "&eClick here &3to generate a command to re-add rank")
								.suggest("/hoh addrank " + player.getName() + " " + getRankCommandArgs(rankHistory)));
						return;
					}
					send(PREFIX + "Could not find the rank to delete");
				})
				.open(player());
	}

	private String getRankCommandArgs(RankHistory rankHistory) {
		String command = (rankHistory.isCurrent() ? "Current" : "Former") + " " + rankHistory.getRank() + " ";
		if (rankHistory.getPromotionDate() != null)
			command += dateFormat(rankHistory.getPromotionDate()) + " ";
		if (rankHistory.getResignationDate() != null)
			command += dateFormat(rankHistory.getResignationDate());
		return command.trim();
	}

	@Permission("hoh.edit")
	@Path("clear <player>")
	void clear(OfflinePlayer player) {
		HallOfHistory history = service.get(player.getUniqueId());
		history.getRankHistory().clear();
		service.save(history);
		send(PREFIX + "Cleared all data for &e" + player.getName());
	}

	@Path("setwarp")
	@Permission("hoh.edit")
	void setWarp() {
		runCommand("blockcenter");
		tasks().wait(3, () -> runCommand("warps set hallofhistory"));
	}

	@Path("expand")
	@Permission("hoh.edit")
	void expand() {
		send(PREFIX + "Expanding HOH. &4&lDon't move!");
		int wait = 40;
		AtomicReference<Location> newLocation = new AtomicReference<>(player().getLocation());
		tasks().wait(wait, () -> runCommand("/warp hallofhistory"));
		tasks().wait(wait += 20, () -> newLocation.set(player().getLocation().add(16, 0, 0).clone()));
		tasks().wait(wait += 3, () -> runCommand("/pos1"));
		tasks().wait(wait += 3, () -> runCommand("/pos2"));
		tasks().wait(wait += 3, () -> runCommand("/expand 7"));
		tasks().wait(wait += 3, () -> runCommand("/expand 15 s"));
		tasks().wait(wait += 3, () -> runCommand("/expand 15 n"));
		tasks().wait(wait += 3, () -> runCommand("/expand 10 e"));
		tasks().wait(wait += 3, () -> runCommand("/expandv 10"));
		tasks().wait(wait += 3, () -> runCommand("/move 16 e"));
		tasks().wait(wait += 20, () -> player().teleport(newLocation.get()));
		tasks().wait(wait += 5, () -> runCommand("/hoh setwarp"));
		tasks().wait(wait += 5, () -> runCommand("/schem load hoh-expansion"));
		tasks().wait(wait += 20, () -> runCommand("/paste"));
		tasks().wait(wait += 20, () -> runCommand("/contract 17"));
		tasks().wait(wait += 3, () -> runCommand("/expand 1"));
		tasks().wait(wait += 3, () -> runCommand("/contract 1"));
		tasks().wait(wait += 3, () -> runCommand("/contract 12 d"));
		tasks().wait(wait += 3, () -> runCommand("/contracth 5"));
		tasks().wait(wait += 3, () -> runCommand("/contract 3 u"));
		tasks().wait(wait += 3, () -> runCommand("/cut"));
		tasks().wait(wait += 3, () -> runCommand("/expand -1"));
		tasks().wait(wait += 3, () -> runCommand("/contract -1"));
		tasks().wait(wait += 3, () -> runCommand("/stack 1"));
		tasks().wait(wait += 3, () -> runCommand("/expand -15"));
		tasks().wait(wait += 3, () -> runCommand("/contract -15"));
		tasks().wait(wait += 3, () -> runCommand("/set stone_slab:8"));
		tasks().wait(wait += 3, () -> runCommand("/desel"));
		send(PREFIX + "Expansion complete! Took &e" + (wait / 20) + " &3seconds");
	}

	@Path("about <about...>")
	void about(String about) {
		NerdService service = new NerdService(getPlugin());
		Nerd nerd = service.get(player());
		nerd.setAbout(stripColor(about));
		service.save(nerd);
		send(PREFIX + "Set your about to: &e" + nerd.getAbout());
	}

}
