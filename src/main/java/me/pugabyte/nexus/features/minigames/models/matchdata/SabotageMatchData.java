package me.pugabyte.nexus.features.minigames.models.matchdata;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import eden.utils.TimeUtils;
import lombok.Data;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.features.menus.sabotage.AbstractVoteScreen;
import me.pugabyte.nexus.features.menus.sabotage.ResultsScreen;
import me.pugabyte.nexus.features.menus.sabotage.VotingScreen;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.Sabotage;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.MatchData;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.MatchDataFor;
import me.pugabyte.nexus.features.minigames.models.arenas.SabotageArena;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.minigamers.sabotage.MinigamerVoteEvent;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageColor;
import me.pugabyte.nexus.features.minigames.models.sabotage.SabotageTeam;
import me.pugabyte.nexus.models.chat.PublicChannel;
import me.pugabyte.nexus.utils.BossBarBuilder;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Utils;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@MatchDataFor(Sabotage.class)
public class SabotageMatchData extends MatchData {
	public SabotageMatchData(Match match) {
		super(match);
	}

	private final Map<UUID, UUID> votes = new HashMap<>();
	private final BiMap<UUID, SabotageColor> playerColors = HashBiMap.create();
	private LocalDateTime meetingStarted, meetingEnded = LocalDateTime.of(1970, 1, 1, 0, 0);
	private int meetingTaskID = -1;
	private AbstractVoteScreen votingScreen;
	private LocalDateTime roundStarted;
	private final Set<UUID> buttonUsers = new HashSet<>();
	private final BossBar bossbar = new BossBarBuilder().color(ColorType.GREEN).title("&aTask Completion").build();
	private int endMeetingTask = -1;
	private final PublicChannel gameChannel = PublicChannel.builder()
			.name("Sabotage")
			.nickname("!")
			.persistent(false)
			.permission("")
			.color(ChatColor.RED)
			.build();
	private final PublicChannel spectatorChannel = PublicChannel.builder()
			.name("Sabotage Spectator")
			.nickname("X")
			.persistent(false)
			.messageColor(ChatColor.GRAY)
			.color(ChatColor.DARK_GRAY)
			.permission("")
			.build();
	private final Map<UUID, LocalDateTime> killCooldowns = new HashMap<>();

	public SabotageArena getArena() {
		return (SabotageArena) super.getArena();
	}

	public int canButtonIn() {
		return (int) Math.max(Duration.between(LocalDateTime.now(), roundStarted.plusSeconds(getArena().getMeetingCooldown())).getSeconds(), 0);
	}

	public boolean canButton() {
		return canButtonIn() == 0;
	}

	public enum ButtonState {
		COOLDOWN,
		USED,
		USABLE;
	}

	public ButtonState canButton(HasUniqueId reporter) {
		if (!canButton())
			return ButtonState.COOLDOWN;
		if (buttonUsers.contains(reporter.getUniqueId()))
			return ButtonState.USED;
		return ButtonState.USABLE;
	}

	public ButtonState button(HasUniqueId reporter) {
		ButtonState state = canButton(reporter);
		if (state == ButtonState.USABLE)
			buttonUsers.add(reporter.getUniqueId());
		return state;
	}

	public @NotNull SabotageColor getColor(HasUniqueId player) {
		playerColors.computeIfAbsent(player.getUniqueId(), $ -> RandomUtils.randomElement(Arrays.stream(SabotageColor.values()).filter(color -> !playerColors.containsValue(color)).collect(Collectors.toList())));
		return playerColors.get(player.getUniqueId());
	}

	public @Nullable SabotageColor getColorNoCompute(HasUniqueId player) {
		return playerColors.get(player.getUniqueId());
	}

	public boolean hasVoted(HasUniqueId player) {
		return votes.containsKey(player.getUniqueId());
	}

	public @Nullable Minigamer getVote(HasUniqueId player) {
		if (!votes.containsKey(player.getUniqueId()))
			return null;
		return PlayerManager.get(votes.get(player.getUniqueId()));
	}

	public @NotNull Set<Minigamer> getVotesFor(HasUniqueId player) {
		UUID uuid = player == null ? null : player.getUniqueId();
		return votes.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), uuid)).map(entry -> PlayerManager.get(entry.getKey())).collect(Collectors.toSet());
	}

	public int maxVotes() {
		List<Minigamer> minigamers = match.getAliveMinigamers();
		minigamers.add(null);
		return Utils.getMax(minigamers, minigamer -> getVotesFor(minigamer).size()).getInteger();
	}

	public boolean waitingToVote() {
		return LocalDateTime.now().isBefore(meetingStarted.plusSeconds(Sabotage.VOTING_DELAY));
	}

	public int votingStartsIn() {
		return waitingToVote() ? (1 + (int) Duration.between(LocalDateTime.now(), meetingStarted.plusSeconds(Sabotage.VOTING_DELAY)).getSeconds()) : 0;
	}

	public boolean vote(HasUniqueId voter, HasUniqueId target) {
		MinigamerVoteEvent event = new MinigamerVoteEvent(PlayerManager.get(voter), PlayerManager.get(target), (VotingScreen) votingScreen);
		event.setCancelled(votes.containsKey(voter.getUniqueId()) || waitingToVote());
		if (event.callEvent()) {
			votes.put(voter.getUniqueId(), target == null ? null : target.getUniqueId());
			if (match.getAliveMinigamers().size() == votes.size())
				endMeeting();
		}
		return event.isCancelled();
	}

	public void clearVotes() {
		votes.clear();
	}

	public boolean isMeetingActive() {
		return meetingTaskID != -1;
	}

	public void startMeeting(Minigamer origin) {
		meetingStarted = LocalDateTime.now();
		votingScreen = new VotingScreen(origin);
		meetingTaskID = match.getTasks().repeat(0, 2, () -> match.getMinigamers().forEach(minigamer -> {
			InventoryView openInv = minigamer.getPlayer().getOpenInventory();
			if (LocationUtils.blockLocationsEqual(minigamer.getPlayer().getLocation(), getArena().getMeetingLocation())) {
				if (openInv.getType() == InventoryType.CRAFTING) return;
				if (openInv.getTitle().equals(votingScreen.getInventory().getTitle())) return;
			} else
				minigamer.teleport(getArena().getMeetingLocation());
			openInv.close();
			votingScreen.open(minigamer);
		}));

		match.getMinigamers().forEach(minigamer -> {
			PlayerUtils.hidePlayers(minigamer, match.getMinigamers());
			minigamer.teleport(getArena().getMeetingLocation());
			votingScreen.open(minigamer);
			PlayerUtils.giveItem(minigamer, Sabotage.VOTING_ITEM.get());
		});
		endMeetingTask = match.getTasks().wait(TimeUtils.Time.SECOND.x(Sabotage.MEETING_LENGTH + Sabotage.VOTING_DELAY), this::endMeeting);
	}

	public void endMeeting() {
		if (votingScreen instanceof ResultsScreen)
			return;
		AbstractVoteScreen oldScreen = votingScreen;
		votingScreen = new ResultsScreen();
		meetingEnded = LocalDateTime.now();
		match.getTasks().cancel(meetingTaskID);
		match.getTasks().cancel(endMeetingTask);
		meetingTaskID = -1;
		endMeetingTask = -1;
		match.getMinigamers().forEach(minigamer -> {
			oldScreen.close(minigamer);
			votingScreen.open(minigamer);
			minigamer.getPlayer().getInventory().remove(Sabotage.VOTING_ITEM.get());
		});
		match.getTasks().wait(TimeUtils.Time.SECOND.x(Sabotage.POST_MEETING_DELAY), () -> {
			match.getMinigamers().forEach(minigamer -> votingScreen.close(minigamer));
			Minigamer ejected = null;
			int votes = getVotesFor(null).size();
			boolean tie = false;
			for (Minigamer minigamer : match.getAliveMinigamers()) {
				int mVotes = getVotesFor(minigamer).size();
				if (mVotes > votes) {
					ejected = minigamer;
					votes = mVotes;
					tie = false;
				} else if (mVotes == votes) {
					ejected = null;
					tie = true;
				}
			}

			String ejectedName;
			if (ejected != null) {
				ejectedName = ejected.getNickname();
			} else
				ejectedName = "Nobody";

			String display = ejectedName + " was ejected.";
			if (ejected == null)
				display += " (" + (tie ? "Tied" : "Skipped") + ")";

			// TODO: true animation
			match.showTitle(Title.title(Component.empty(), new JsonBuilder(display).build(), Title.Times.of(fade, Duration.ofSeconds(7), fade)));
			match.playSound(Sound.sound(org.bukkit.Sound.ENTITY_PLAYER_SPLASH_HIGH_SPEED, Sound.Source.PLAYER, 1.0F, 1.0F));
			clearVotes();
			votingScreen = null;
			roundStarted = LocalDateTime.now();
			match.getMinigamers().forEach(Minigamer::respawn);

			SabotageTeam team = SabotageTeam.of(ejected);
			if (team == SabotageTeam.JESTER) {
				ejected.scored();
				match.end();
			} else if (ejected != null)
				match.getMechanic().onDeath(new MinigamerDeathEvent(ejected));
		});
	}

	private static final Duration fade = Duration.ofSeconds(1).dividedBy(2);

	public void setRoundStarted() {
		setRoundStarted(LocalDateTime.now());
	}
}