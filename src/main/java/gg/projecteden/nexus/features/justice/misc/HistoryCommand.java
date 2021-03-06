package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.punishments.Punishment;
import gg.projecteden.nexus.models.punishments.Punishments;
import gg.projecteden.nexus.models.punishments.PunishmentsService;
import gg.projecteden.nexus.utils.JsonBuilder;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

import static java.util.stream.Collectors.toList;

@Permission("group.moderator")
public class HistoryCommand extends _JusticeCommand {
	private final PunishmentsService service = new PunishmentsService();

	public HistoryCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player> [page]")
	void run(Punishments player, @Arg("1") int page) {
		if (player.getPunishments().isEmpty())
			error("No history found");

		send("");
		send(PREFIX + "History of &e" + player.getNickname());

		int perPage = 3;

		BiFunction<Punishment, String, JsonBuilder> formatter = (punishment, index) -> {
			JsonBuilder json = punishment.getType().getHistoryDisplay(punishment);
			int indexInt = Integer.parseInt(index);
			if (indexInt % perPage != 0 && indexInt != player.getPunishments().size())
				json.newline();
			return json;
		};

		List<Punishment> sorted = player.getPunishments().stream()
				.sorted(Comparator.comparing(Punishment::getTimestamp).reversed())
				.collect(toList());

		paginate(sorted, formatter, "/history " + player.getName(), page, perPage);
	}

	@Confirm
	@TabCompleteIgnore
	@Path("delete <player> <id>")
	void delete(Punishments player, @Arg(context = 1) Punishment punishment) {
		player.remove(punishment);
		service.save(player);
		send(PREFIX + "Punishment deleted");
	}

	@ConverterFor(Punishment.class)
	Punishment convertToPunishment(String value, Punishments context) {
		return context.getPunishment(UUID.fromString(value));
	}

}
