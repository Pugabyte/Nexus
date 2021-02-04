package me.pugabyte.nexus.features.minigames.commands;

import com.sk89q.worldguard.protection.flags.Flag;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.ArenaManager;
import me.pugabyte.nexus.features.minigames.managers.MatchManager;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.Mastermind;
import me.pugabyte.nexus.features.minigames.mechanics.common.CheckpointMechanic;
import me.pugabyte.nexus.features.minigames.models.Arena;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.Team;
import me.pugabyte.nexus.features.minigames.models.matchdata.CheckpointMatchData;
import me.pugabyte.nexus.features.minigames.models.matchdata.MastermindMatchData;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import me.pugabyte.nexus.framework.exceptions.preconfigured.MustBeIngameException;
import me.pugabyte.nexus.models.minigamersetting.MinigamerSetting;
import me.pugabyte.nexus.models.minigamersetting.MinigamerSettingService;
import me.pugabyte.nexus.models.warps.WarpService;
import me.pugabyte.nexus.models.warps.WarpType;
import me.pugabyte.nexus.utils.LocationUtils.RelativeLocation;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Aliases({"mgm", "mg"})
@Permission("minigames")
public class MinigamesCommand extends CustomCommand {
	private Minigamer minigamer;

	public MinigamesCommand(CommandEvent event) {
		super(event);
		PREFIX = Minigames.PREFIX;
		if (sender() instanceof Player)
			minigamer = PlayerManager.get(player());
	}

	@Path
	@Permission("use")
	void warp() {
		runCommand("warp minigames");
	}

	@Path("list [filter]")
	@Permission("use")
	void list(String filter) {
		send(PREFIX + ArenaManager.getAll(filter).stream()
				.map(arena -> (MatchManager.find(arena) != null ? "&e" : "&3") + arena.getName())
				.collect(Collectors.joining("&3, ")));
	}

	@Path("join <arena>")
	@Permission("use")
	void join(Arena arena) {
		minigamer.join(arena);
	}

	@Path("(quit|leave)")
	@Permission("use")
	void quit() {
		minigamer.quit();
	}

	@Path("settings bowInOffHand [boolean]")
	@Permission("use")
	void settings_bowInOffHand(Boolean offHand) {
		MinigamerSettingService service = new MinigamerSettingService();
		MinigamerSetting settings = service.get(player());
		if (offHand == null)
			offHand = !settings.isBowInOffHand();

		settings.setBowInOffHand(offHand);
		send(PREFIX + "Bows will now spawn in your " + (offHand ? "offhand" : "hotbar"));
		service.save(settings);
	}

	@Path("start [arena]")
	@Permission("manage")
	void start(@Arg("current") Arena arena) {
		getRunningMatch(arena).start();
	}

	@Path("end [arena]")
	@Permission("manage")
	void end(@Arg("current") Arena arena) {
		getRunningMatch(arena).end();
	}

	@Path("debug [arena]")
	@Permission("manage")
	void debug(@Arg("current") Arena arena) {
		send(arena.toString());
	}

	@Permission("manage")
	@Path("signs join <arena>")
	void joinSign(Arena arena) {
		Sign sign = getTargetSignRequired();
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aJoin"));
		String arenaName = arena.getName();
		if (arenaName.length() > 15) {
			sign.setLine(2, arenaName.substring(0, 15));
			sign.setLine(3, arenaName.substring(15));
		} else {
			sign.setLine(2, arena.getName());
			sign.setLine(3, "");
		}

		sign.update();
	}

	@Permission("manage")
	@Path("signs quit")
	void quitSign() {
		Sign sign = getTargetSignRequired();
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aQuit"));
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	@Permission("manage")
	@Path("signs lobby")
	void lobbySign() {
		Sign sign = getTargetSignRequired();
		sign.setLine(0, StringUtils.colorize("&0&l< &1Minigames &0&l>"));
		sign.setLine(1, StringUtils.colorize("&aLobby"));
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}

	@Path("setTime <seconds>")
	@Permission("manage")
	void setTime(int seconds) {
		if (minigamer.getMatch() == null)
			error("You are not in a match");
		minigamer.getMatch().getTimer().setTime(seconds);
		minigamer.getMatch().getTimer().broadcastTimeLeft();
	}

	@Path("flagParticle")
	@Permission("manage")
	void flagParticle() {
		me.pugabyte.nexus.features.minigames.models.matchdata.Flag.particle(minigamer);
	}

	@Path("create <name>")
	@Permission("manage")
	void create(String name) {
		if (ArenaManager.exists(name))
			send(PREFIX + "Editing arena &e" + name + "&3");
		else {
			Arena arena = new Arena(name);
			arena.write();
			send(PREFIX + "Creating arena &e" + name + "&3");
		}

		Minigames.getMenus().openArenaMenu(player(), ArenaManager.get(name));
	}

	@Path("copy <from> <to>")
	@Permission("manage")
	void copy(Arena arena, String name) {
		if (ArenaManager.exists(name))
			error("&e" + name + " already exists");

		Arena copy = ArenaManager.convert(arena, arena.getClass());
		copy.setId(ArenaManager.getNextId());
		copy.setName(name);
		copy.setDisplayName(name);
		copy.write();
		send(PREFIX + "Creating arena &e" + name + "&3");
		send(PREFIX + "&cRecommended: &3Edit .yml file to remove locations");
		Minigames.getMenus().openArenaMenu(player(), ArenaManager.get(name));
	}

	@Path("edit <arena>")
	@Permission("manage")
	void edit(Arena arena) {
		Minigames.getMenus().openArenaMenu(player(), arena);
	}

	@Path("warp <arena>")
	@Permission("manage")
	void teleport(Arena arena) {
		arena.teleport(minigamer);
	}

	@Path("(tp|teleport) <player> [player]")
	@Permission("manage")
	void teleport(Minigamer minigamer1, Minigamer minigamer2) {
		if (minigamer2 == null)
			minigamer.teleport(minigamer1.getPlayer().getLocation());
		else
			minigamer1.teleport(minigamer2.getPlayer().getLocation());
	}

	@Path("tppos <player> <x> <y> <z> [yaw] [pitch]")
	@Permission("manage")
	void teleport(Minigamer minigamer, String x, String y, String z, String yaw, String pitch) {
		Location location = minigamer.getPlayer().getLocation();
		RelativeLocation.modify(location).x(x).y(y).z(z).yaw(yaw).pitch(pitch).update();
		minigamer.teleport(location);
	}

	@Path("(delete|remove) <arena>")
	@Permission("manage")
	void remove(Arena arena) {
		Minigames.getMenus().openDeleteMenu(player(), arena);
	}

	@Path("(reload|read) [arena]")
	@Permission("manage")
	void reload(@Arg(tabCompleter = Arena.class) String arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.read();
		else
			ArenaManager.read(arena);

		send(PREFIX + "Reload time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Async
	@Path("(save|write) [arena]")
	@Permission("manage")
	void save(Arena arena) {
		long startTime = System.currentTimeMillis();

		if (arena == null)
			ArenaManager.write();
		else
			ArenaManager.write(arena);

		send(PREFIX + "Save time took " + (System.currentTimeMillis() - startTime) + "ms");
	}

	@Path("autoreset [boolean]")
	@Permission("use")
	void autoreset(Boolean autoreset) {
		Match match = minigamer.getMatch();
		if (!minigamer.isPlaying())
			error("You must be playing a checkpoint game to use that command");

		if (!(match.getMechanic() instanceof CheckpointMechanic))
			error("You are not in a checkpoint game");

		CheckpointMatchData matchData = match.getMatchData();
		matchData.autoreset(minigamer, autoreset);
		if (matchData.isAutoresetting(minigamer))
			send(PREFIX + "Enabled &eAuto Reset");
		else
			send(PREFIX + "Disabled &eAuto Reset");
	}

	@Path("addSpawnpoint <arena> [team]")
	void addSpawnpoint(Arena arena, @Arg(context = 1) Team team) {
		List<Team> teams = arena.getTeams();

		if (team == null) {
			if (teams.size() != 1)
				error("There is more than one team in that arena, you must specify which one");

			teams.get(0).getSpawnpoints().add(player().getLocation());
			arena.write();
			send(PREFIX + "Spawnpoint added");
			return;
		}

		team.getSpawnpoints().add(player().getLocation());
		arena.write();
		send(PREFIX + "Spawnpoint added");
	}

	private static String inviteCommand;
	private static String inviteMessage;

	private void updateInvite() {
		boolean isMinigameNight = false;
		LocalDateTime date = LocalDateTime.now();
		DayOfWeek dow = date.getDayOfWeek();

		if (dow.equals(DayOfWeek.SATURDAY)) {
			int hour = date.getHour();
			if (hour > 15 && hour < 18) {
				isMinigameNight = true;
			}
		}

		boolean canUse = false;
		if (!isMinigameNight)
			canUse = true;
		if (player().hasPermission("minigames.invite"))
			canUse = true;

		if (!canUse)
			permissionError();

		WorldGuardUtils WGUtils = new WorldGuardUtils(player());
		if (!WGUtils.isInRegion(player().getLocation(), "minigamelobby"))
			error("You must be in the Minigame Lobby to use this command");

		Collection<Player> players = WGUtils.getPlayersInRegion("minigamelobby");
		int count = players.size() - 1;
		if (count == 0)
			error("There is no one to invite!");

		if (WGUtils.isInRegion(player().getLocation(), "screenshot")) {
			inviteCommand = "warp screenshot";
			inviteMessage = "take a screenshot";
		} else {
			Sign sign = getTargetSignRequired();
			String line2 = stripColor(sign.getLine(1)).toLowerCase();
			if (line2.contains("screenshot"))
				error("Stand in the screenshot area then run the command (sign not needed)");
			if (!line2.contains("join"))
				error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

			String prefix = "";
			String line1 = stripColor(sign.getLine(0)).toLowerCase();
			if (line1.contains("[minigame]") || line1.contains("< minigames >"))
				prefix = "mgm";
			else
				error("Cannot parse sign. If you believe this is an error, make a GitHub ticket with information and screenshots.");

			String line3 = stripColor(sign.getLine(2)) + stripColor(sign.getLine(3));
			inviteCommand = prefix + " join " + line3;
			inviteMessage = line3;
		}
	}

	private void sendInvite(Collection<? extends Player> players) {
		String sender = player().getName();
		send("&3Invite sent to &e" + (players.size() - 1) + " &3players for &e" + inviteMessage);
		for (Player player : players) {
			if (player.equals(player()))
				continue;

			send(player, json("")
					.newline()
					.next(" &e" + sender + " &3has invited you to &e" + inviteMessage).group()
					.newline()
					.next("&e Click here to &a&laccept")
					.command("/mgm accept")
					.hover("&eClick &3to accept"));
		}
	}

	@Path("invite")
	void invite() {
		updateInvite();
		sendInvite(new WorldGuardUtils(player()).getPlayersInRegion("minigamelobby"));
	}

	@Permission("manage")
	@Path("inviteAll")
	void inviteAll() {
		updateInvite();
		sendInvite(Bukkit.getOnlinePlayers());
	}

	@Path("accept")
	void acceptInvite() {
		if (inviteCommand == null)
			error("There is no pending game invite");

		if (player().getWorld() != Minigames.getWorld()) {
			new WarpService().get("minigames", WarpType.NORMAL).teleport(player());
			Tasks.wait(5, this::acceptInvite);
		} else
			runCommand(inviteCommand);
	}

	@Path("holeinthewall flag <arena> <regionType> <flag> <setting...>")
	void holeInTheWallFlag(Arena arena, String regionType, Flag<?> flag, String setting) {
		for (int i = 1; i <= arena.getMaxPlayers(); i++)
			runCommand("rg flag holeinthewall_" + arena.getName() + "_" + regionType + "_" + i + " " + flag + " " + setting);
	}

	@Path("mastermind showAnswer")
	@Permission("group.admin")
	void mastermindShowAnswer() {
		if (!minigamer.isPlaying(Mastermind.class))
			error("You must be playing Mastermind to use this command");

		MastermindMatchData matchData = minigamer.getMatch().getMatchData();
		send(matchData.getAnswer().toString());
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("mastermind playAgain")
	void mastermindPlayAgain() {
		if (!minigamer.isPlaying(Mastermind.class))
			error("You must be playing Mastermind to use this command");

		MastermindMatchData matchData = minigamer.getMatch().getMatchData();
		matchData.reset(minigamer);
	}

	private Match getRunningMatch(Arena arena) {
		Match match = MatchManager.find(arena);

		if (match == null)
			error("There is no match running for that arena");

		return match;
	}

	@ConverterFor(Arena.class)
	Arena convertToArena(String value) {
		if ("current".equalsIgnoreCase(value))
			if (minigamer != null)
				if (minigamer.getMatch() != null)
					return minigamer.getMatch().getArena();
				else
					throw new InvalidInputException("You are not currently in a match");
			else
				throw new MustBeIngameException();
		else
			return ArenaManager.find(value);
	}

	@TabCompleterFor(Arena.class)
	List<String> arenaTabComplete(String filter) {
		return ArenaManager.getNames(filter);
	}

	@ConverterFor(Minigamer.class)
	Minigamer convertToMinigamer(String value) {
		if ("self".equalsIgnoreCase(value))
			return minigamer;
		OfflinePlayer player = PlayerUtils.getPlayer(value);
		if (!player.isOnline())
			throw new PlayerNotOnlineException(player);
		return PlayerManager.get(player.getPlayer());
	}

	@TabCompleterFor(Minigamer.class)
	List<String> tabCompleteMinigamer(String filter) {
		return tabCompletePlayer(filter);
	}

	@ConverterFor(Team.class)
	Team convertToTeam(String value, Arena context) {
		if ("current".equalsIgnoreCase(value))
			return minigamer.getTeam();

		if (context == null)
			context = minigamer.getMatch().getArena();

		return context.getTeams().stream()
				.filter(team -> team.getName().startsWith(value))
				.findFirst()
				.orElseThrow(() -> new InvalidInputException("Team not found"));
	}

	@TabCompleterFor(Team.class)
	List<String> tabCompleteTeam(String filter, Arena context) {
		if (context == null)
			context = minigamer.getMatch().getArena();

		if (context == null)
			return new ArrayList<>();

		return context.getTeams().stream()
				.map(Team::getName)
				.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}
}
