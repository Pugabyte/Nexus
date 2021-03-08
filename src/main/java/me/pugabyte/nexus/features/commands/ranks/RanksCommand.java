package me.pugabyte.nexus.features.commands.ranks;

import me.pugabyte.nexus.features.menus.BookBuilder;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.JsonBuilder;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class RanksCommand extends CustomCommand {

	public RanksCommand(CommandEvent event) {
		super(event);
	}

	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/YY");

	@Path
	void ranks() {
		line(5);
		send("&3Here is a list of server ranks. &eClick &3on one to view more info. You can tell what rank a person is by the &ecolor and format of their name&3.");
		send(json("&3Please do not ask for ranks. You have to ")
				.group()
				.next("&eearn")
				.hover("&eClick here &3for a basic guide to ranking up")
				.command("/faq ranks ranks")
				.group()
				.next("&3 them")
		);
		line();
		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive()) return;
			JsonBuilder builder = new JsonBuilder("&3- " + rank.withColor());
			builder.command("/" + rank.name().toLowerCase());
			if (Rank.of(player()) == rank)
				builder.next("  &e&o<-- You are here!");

			send(builder);
		});
		line();
	}

	static void ranksReturn(Player player) {
		new JsonBuilder()
				.next("&f &3&m<  &e Back")
				.command("/ranks")
				.send(player);
	}

	// TODO: Maybe use 1.16 colors to make this look better?
	@Path("book")
	@Permission("group.staff")
	public void bookMenu() {
		BookBuilder.WrittenBookMenu bookBuilder = new BookBuilder.WrittenBookMenu();

		AtomicReference<JsonBuilder> jsonBuilder = new AtomicReference<>(new JsonBuilder());

		jsonBuilder.get().next("Click a rank to view more information.").line();

		Arrays.asList(Rank.values()).forEach(rank -> {
			if (!rank.isActive()) return;

			String formattedRank = rank.withColor();
			if (rank.equals(Rank.GUEST))
				formattedRank = colorize("&8" + rank.plain());
			else if (rank.equals(Rank.MEMBER))
				formattedRank = colorize("&7" + rank.plain());

			jsonBuilder.get().next("&3[+] " + formattedRank);
			jsonBuilder.get().command("/" + rank.name().toLowerCase());
			if (Rank.of(player()) == rank)
				jsonBuilder.get().next("  &0&o<-- You");
			jsonBuilder.get().newline().group();
		});

		bookBuilder.addPage(jsonBuilder.get());
		bookBuilder.open(player());
	}
}
