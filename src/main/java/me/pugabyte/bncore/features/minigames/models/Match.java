package me.pugabyte.bncore.features.minigames.models;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.minigames.managers.MatchManager;
import me.pugabyte.bncore.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchBroadcastEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchEndEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchInitializeEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchQuitEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchStartEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.bncore.features.minigames.models.events.matches.teams.TeamScoredEvent;
import me.pugabyte.bncore.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.bncore.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.bncore.features.minigames.models.scoreboards.MinigameScoreboard;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.bncore.utils.Utils.colorize;

@Data
public class Match {
	@NonNull
	private Arena arena;
	@ToString.Exclude
	private List<Minigamer> minigamers = new ArrayList<>();
	private boolean initialized = false;
	private boolean started = false;
	private boolean ended = false;
	private Map<Team, Integer> scores = new HashMap<>();
	private MatchTimer timer;
	private MinigameScoreboard scoreboard;
	private MinigameScoreboard.Teams scoreboardTeams;
	private ArrayList<Hologram> holograms = new ArrayList<>();
	private MatchData matchData;
	private MatchTasks tasks;

	public Minigamer getMinigamer(Player player) {
		for (Minigamer minigamer : minigamers)
			if (minigamer.getPlayer().equals(player))
				return minigamer;

		return null;
	}

	public List<Player> getPlayers() {
		return minigamers.stream().map(Minigamer::getPlayer).collect(Collectors.toList());
	}

	public List<Team> getTeams() {
		return minigamers.stream().filter(minigamer -> minigamer.getTeam() != null).map(Minigamer::getTeam).collect(Collectors.toList());
	}

	public <T extends Arena> T getArena() {
		return (T) arena;
	}

	public <T extends MatchData> T getMatchData() {
		return (T) matchData;
	}

	public boolean join(Minigamer minigamer) {
		initialize();

		if (started && !arena.canJoinLate()) {
			minigamer.tell("This match has already started");
			return false;
		}

		if (minigamers.size() >= arena.getMaxPlayers()) {
			minigamer.tell("This match is full");
			return false;
		}

		MatchJoinEvent event = new MatchJoinEvent(this, minigamer);
		Utils.callEvent(event);
		if (event.isCancelled()) return false;

		minigamers.add(minigamer);

		try {
			arena.getMechanic().processJoin(minigamer);
			arena.getMechanic().onJoin(event);
		} catch (Exception ex) { ex.printStackTrace(); }

		if (scoreboard != null) scoreboard.handleJoin(minigamer);
		if (scoreboardTeams != null) scoreboardTeams.handleJoin(minigamer);

		return true;
	}

	void quit(Minigamer minigamer) {
		if (!minigamers.contains(minigamer)) return;

		MatchQuitEvent event = new MatchQuitEvent(this, minigamer);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		minigamers.remove(minigamer);
		try { arena.getMechanic().onQuit(event); } catch (Exception ex) { ex.printStackTrace(); }
		minigamer.clearState();
		minigamer.toGamelobby();

		if (scoreboard != null) scoreboard.handleQuit(minigamer);
		if (scoreboardTeams != null) scoreboardTeams.handleQuit(minigamer);

		if (minigamers == null || minigamers.size() == 0)
			end();
	}

	public void start() {
		if (started)
			throw new InvalidInputException("Match already started");

		MatchStartEvent event = new MatchStartEvent(this);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		started = true;
		clearEntities();
		balance();
		initializeScores();
		teleportIn();
		startTimer(); // -> arena.getMechanic().startTimer();
		arena.getMechanic().onStart(event);

		if (scoreboard != null) scoreboard.update();
		if (scoreboardTeams != null) scoreboardTeams.update();
	}

	public void end() {
		if (ended) return;

		MatchEndEvent event = new MatchEndEvent(this);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		ended = true;
		if (tasks != null)
			tasks.end();
		broadcast("Match has ended");
		clearHolograms();
		clearEntities();
		clearStates();
		toGamelobby();
		arena.getMechanic().onEnd(event);
		minigamers = new ArrayList<>();

		if (scoreboard != null) scoreboard.handleEnd();
		if (scoreboardTeams != null) scoreboardTeams.handleEnd();

		MatchManager.remove(this);
	}

	private void initialize() {
		if (!initialized) {
			MatchInitializeEvent event = new MatchInitializeEvent(this);
			Utils.callEvent(event);
			if (event.isCancelled()) return;

			initializeMatchData();
			tasks = new MatchTasks();
			scoreboard = MinigameScoreboard.Factory.create(this);
			scoreboardTeams = new MinigameScoreboard.Teams(this);
			arena.getMechanic().onInitialize(event);
			initialized = true;
		}
	}

	private void initializeMatchData() {
		try {
			String path = this.getClass().getPackage().getName();
			Set<Class<? extends MatchData>> matchDataTypes = new Reflections(path + ".matchdata")
					.getSubTypesOf(MatchData.class);

			matchDataTypes:
			for (Class<?> matchDataType : matchDataTypes)
				for (Class<? extends Mechanic> superclass : arena.getMechanic().getSuperclasses())
					if (matchDataType.getAnnotation(MatchDataFor.class) != null)
						if (matchDataType.getAnnotation(MatchDataFor.class).value().equals(superclass)) {
							matchData = (MatchData) matchDataType.getConstructor(Match.class).newInstance(this);
							break matchDataTypes;
						}

		} catch (Exception ex) {
			BNCore.log("Error initializing match data for " + arena.getMechanic().getName());
			ex.printStackTrace();
		}
	}

	private void startTimer() {
		timer = new MatchTimer(this, arena.getSeconds());
	}

	private void stopTimer() {
		if (timer != null)
			timer.stop();
	}

	private void clearEntities() {
		// TODO: Clearing dropped entities & arrows
	}

	public void clearHolograms() {
		holograms.forEach(Hologram::delete);
		holograms.clear();
	}

	private void balance() {
		arena.getMechanic().balance(minigamers);
		if (arena.getMechanic() instanceof TeamMechanic)
			minigamers.forEach(minigamer -> minigamer.tell("You are on team " + minigamer.getTeam().getColoredName()));
	}

	private void initializeScores() {
		arena.getTeams().forEach(team -> scores.put(team, 0));
	}

	private void teleportIn() {
		arena.getTeams().forEach(team -> team.spawn(minigamers));
	}

	public void teleportIn(Minigamer minigamer) {
		minigamer.getTeam().spawn(Collections.singletonList(minigamer));
	}

	private void clearStates() {
		minigamers.forEach(Minigamer::clearState);
	}

	private void toGamelobby() {
		minigamers.forEach(Minigamer::toGamelobby);
	}

	public void scored(Team team) {
		scored(team, 1);
	}

	public void scored(Team team, int score) {
		setScore(team, scores.getOrDefault(team, 0) + score);
	}

	public void setScore(Team team, int score) {
		int diff = score - scores.getOrDefault(team, 0);

		TeamScoredEvent event = new TeamScoredEvent(this, team, diff);
		Utils.callEvent(event);
		if (event.isCancelled()) return;

		scores.put(team, scores.getOrDefault(team, 0) + event.getAmount());
		scoreboard.update();
		if (scores.getOrDefault(team, 0) >= arena.getWinningScore())
			end();
	}

	public void broadcast(String message) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message);
		Utils.callEvent(event);
		if (!event.isCancelled()) {
			minigamers.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
		}
	}

	public void broadcast(String message, Team team) {
		MatchBroadcastEvent event = new MatchBroadcastEvent(this, message, team);
		Utils.callEvent(event);
		if (!event.isCancelled()) {
			minigamers.stream()
					.filter(minigamer -> minigamer.getTeam().equals(event.getTeam()))
					.collect(Collectors.toSet())
					.forEach(minigamer -> minigamer.tell(colorize(event.getMessage())));
		}
	}

	public List<Minigamer> getAlivePlayers() {
		return minigamers.stream().filter(Minigamer::isAlive).collect(Collectors.toList());
	}

	public List<Minigamer> getUnassignedPlayers() {
		return minigamers.stream()
				.filter(waiting -> waiting.getTeam() == null)
				.collect(Collectors.toList());
	}

	public boolean isMechanic(Mechanic mechanic) {
		return isMechanic(mechanic.getClass());
	}

	public boolean isMechanic(Class<? extends Mechanic> mechanic) {
		return mechanic.isInstance(getArena().getMechanic());
	}

	public class MatchTimer {
		private Match match;
		private List<Integer> broadcasts = Arrays.asList((60 * 10), (60 * 5), 60, 30, 15, 5, 4, 3, 2, 1);
		private int taskId;
		@Getter
		@Setter
		private int time;

		MatchTimer(Match match, int time) {
			this.match = match;
			this.time = time;
			start();
		}

		public void addTime(int time) {
			this.time += time;
		}

		void start() {
			if (time > 0) {
				match.getTasks().wait(1, () -> match.broadcast("&e" + (time + 1) + " &7seconds left..."));
				taskId = match.getTasks().repeat(0, 20, () -> {
					if (--time > 0) {
						if (broadcasts.contains(time)) {
							match.broadcast("&e" + time + " &7seconds left...");
							match.getPlayers().forEach(player -> player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, .75F, .6F));
						}
					} else {
						match.end();
						stop();
					}
				});
			} else {
				taskId = match.getTasks().repeat(0, 20, () -> {
					MatchTimerTickEvent event = new MatchTimerTickEvent(match, ++time);
					Utils.callEvent(event);
				});
			}
		}

		void stop() {
			Tasks.cancel(taskId);
		}
	}

	public class MatchTasks {
		private List<Integer> taskIds = new ArrayList<>();

		void end() {
			taskIds.forEach(this::cancel);
		}

		public void cancel(int taskId) {
			Tasks.cancel(taskId);
		}

		public void register(int taskId) {
			taskIds.add(taskId);
		}

		public int wait(long delay, Runnable runnable) {
			int taskId = Tasks.wait(delay, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int repeat(long startDelay, long interval, Runnable runnable) {
			int taskId = Tasks.repeat(startDelay, interval, runnable);
			taskIds.add(taskId);
			return taskId;
		}

		public int async(Runnable runnable) {
			int taskId = Tasks.async(runnable);
			taskIds.add(taskId);
			return taskId;
		}
	}

}
