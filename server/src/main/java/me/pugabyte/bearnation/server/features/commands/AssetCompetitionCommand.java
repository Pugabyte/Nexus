package me.pugabyte.bearnation.server.features.commands;

import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.JsonBuilder;
import me.pugabyte.bearnation.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.bearnation.server.models.assetcompetition.AssetCompetition;
import me.pugabyte.bearnation.server.models.assetcompetition.AssetCompetitionService;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.List;

@Aliases("assetcomp")
public class AssetCompetitionCommand extends CustomCommand {
	AssetCompetitionService service = new AssetCompetitionService();
	AssetCompetition assetCompetition;

	public AssetCompetitionCommand(@NonNull CommandEvent event) {
		super(event);
		assetCompetition = service.get(player());
	}

	@Path("submit")
	void submit() {
		assetCompetition.setLocation(player().getLocation());
		service.save(assetCompetition);
		send(PREFIX + "You have submitted your build for the asset competition. The judges will be by soon! Thank you and good luck!");
	}

	@Path("list")
	@Permission("group.staff")
	void list() {
		List<AssetCompetition> all = service.getAll();
		if (all.size() == 0)
			error("There are no available submissions");

		JsonBuilder builder = new JsonBuilder();
		for (AssetCompetition assetCompetition : all) {
			if (!builder.isInitialized())
				builder.initialize();
			else
				builder.next("&e, ").group();

			builder.next("&3" + assetCompetition.getOfflinePlayer().getName())
					.command(getAliasUsed() + " tp " + assetCompetition.getOfflinePlayer().getName())
					.group();
		}

		line();
		send(PREFIX + "&3List of submissions &e(Click to teleport)");
		send(builder);
	}

	@Path("(view|tp) <player>")
	@Permission("group.staff")
	void view(OfflinePlayer player) {
		assetCompetition = service.get(player);
		if (assetCompetition.getLocation() == null)
			error("That player has not submitted an asset");

		player().teleport(assetCompetition.getLocation(), TeleportCause.COMMAND);
	}

	@Path("clear")
	@Permission("group.seniorstaff")
	void clear() {
		ConfirmationMenu.builder()
				.onConfirm(e -> {
					service.deleteAll();
					send(PREFIX + "All submissions cleared");
				})
				.open(player());
	}

}
