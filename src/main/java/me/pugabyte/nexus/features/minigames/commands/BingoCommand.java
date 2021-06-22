package me.pugabyte.nexus.features.minigames.commands;

import eden.utils.EnumUtils;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.Bingo;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.matchdata.BingoMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.challenge.common.Challenge;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.bingo.progress.common.IChallengeProgress;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BingoCommand extends CustomCommand {

	private Minigamer minigamer;

	private Match match;
	private Bingo mechanic;
	private BingoMatchData matchData;

	public BingoCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent()) {
			minigamer = PlayerManager.get(player());
			if (!minigamer.isIn(Bingo.class))
				error("You must be playing Bingo to use this command");

			match = minigamer.getMatch();
			mechanic = minigamer.getMatch().getMechanic();
			matchData = minigamer.getMatch().getMatchData();
		}
	}

	@Path
	void menu() {
		new BingoMenu(minigamer).open(player());
	}

	private static class BingoMenu extends MenuUtils implements InventoryProvider {
		private final Minigamer minigamer;
		private final BingoMatchData matchData;

		public BingoMenu(Minigamer minigamer) {
			this.minigamer = minigamer;
			if (!minigamer.isIn(Bingo.class))
				throw new InvalidInputException("You must be playing Bingo to use this command");

			matchData = minigamer.getMatch().getMatchData();
		}

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
					.provider(this)
					.title("Bingo")
					.size(6, 9)
					.build()
					.open(viewer, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			int row = 1;
			int column = 2;

			for (Challenge[] array : matchData.getChallenges()) {
				for (Challenge challenge : array) {
					final ItemBuilder builder = new ItemBuilder(Material.STONE).name(EnumUtils.prettyName(challenge.name()));
					final IChallengeProgress progress = matchData.getProgress(minigamer, challenge.getChallenge().getProgressClass());
					if (progress.isCompleted(challenge.getChallenge())) {
						builder.glow();
						builder.lore("&aCompleted");
					}

					contents.set(row, column, ClickableItem.empty(builder.build()));
					++column;
				}

				++row;
				column = 2;
			}
		}

	}

}