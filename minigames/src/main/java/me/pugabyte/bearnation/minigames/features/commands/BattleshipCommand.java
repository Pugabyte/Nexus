package me.pugabyte.bearnation.minigames.features.commands;

import lombok.Getter;
import lombok.NonNull;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Aliases;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Permission;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.api.utils.Utils.CardinalDirection;
import me.pugabyte.bearnation.minigames.features.managers.PlayerManager;
import me.pugabyte.bearnation.minigames.features.mechanics.Battleship;
import me.pugabyte.bearnation.minigames.features.mechanics.Battleship.ShipType;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.Team;
import me.pugabyte.bearnation.minigames.features.models.arenas.BattleshipArena;
import me.pugabyte.bearnation.minigames.features.models.matchdata.BattleshipMatchData;
import me.pugabyte.bearnation.minigames.features.models.matchdata.BattleshipMatchData.Grid.Coordinate;
import me.pugabyte.bearnation.minigames.features.models.mechanics.MechanicType;

import java.util.Arrays;

@Aliases("bs")
@Permission("group.staff")
public class BattleshipCommand extends CustomCommand {
	@Getter
	private static boolean debug;

	private Minigamer minigamer;
	private Battleship mechanic;

	private Match match;
	private BattleshipArena arena;
	private BattleshipMatchData matchData;

	public BattleshipCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer()) {
			minigamer = PlayerManager.get(player());
			mechanic = (Battleship) MechanicType.BATTLESHIP.get();
			if (minigamer.isPlaying(Battleship.class)) {
				match = minigamer.getMatch();
				arena = minigamer.getMatch().getArena();
				matchData = minigamer.getMatch().getMatchData();
			}
		}
	}

	@Path("kit")
	void kit() {
		Arrays.asList(ShipType.values()).forEach(shipType -> Utils.giveItem(player(), shipType.getItem()));
	}

	@Path("debug")
	void debug() {
		debug = !debug;
		send(PREFIX + "Debug " + (debug ? "&aenabled" : "&cdisabled"));
	}

	@Path("getChatGrid")
	void getChatGrid() {
		matchData.getGrid(minigamer.getTeam()).getChatGrid().forEach(this::send);
	}

	@Path("pasteShip <shipType> <direction>")
	void pasteShip(ShipType shipType, CardinalDirection direction) {
		mechanic.pasteShip(shipType, player().getLocation(), direction);
	}

	@Path("toKitLocation <coordinate>")
	void toKitLocation(Coordinate coordinate) {
		minigamer.teleport(Utils.getCenteredLocation(coordinate.getKitLocation()));
	}

	@Path("toPegLocation <coordinate>")
	void toPegLocation(Coordinate coordinate) {
		minigamer.teleport(Utils.getCenteredLocation(coordinate.getPegLocation()));
	}

	@Path("aim <coordinate>")
	void aim(Coordinate coordinate) {
		coordinate.aim();
	}

	@Path("fire <coordinate>")
	void fire(Coordinate coordinate) {
		coordinate.fire();
	}

	@Path("start")
	void start() {
		mechanic.start(match);
	}

	@ConverterFor(Coordinate.class)
	Coordinate convertToCoordinate(String value, Team context) {
		if (context == null)
			context = minigamer.getTeam();

		return matchData.getGrid(context).getCoordinate(value);
	}

}
