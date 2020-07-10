package me.pugabyte.bearnation.minigames.features.mechanics;

import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Match;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.Team;
import me.pugabyte.bearnation.minigames.features.models.events.matches.MatchStartEvent;
import me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bearnation.minigames.features.models.mechanics.multiplayer.teams.UnbalancedTeamMechanic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class Infection extends UnbalancedTeamMechanic {

	@Override
	public String getName() {
		return "Infection";
	}

	@Override
	public String getDescription() {
		return "Zombies kill humans";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.ZOMBIE_HEAD);
	}

	public List<Minigamer> getZombies(Match match) {
		return match.getAliveMinigamers().stream().filter(minigamer -> minigamer.getTeam().getColor() == ChatColor.RED).collect(Collectors.toList());
	}

	public List<Minigamer> getHumans(Match match) {
		return match.getAliveMinigamers().stream().filter(minigamer -> minigamer.getTeam().getColor() != ChatColor.RED).collect(Collectors.toList());
	}

	@Override
	public void onStart(MatchStartEvent event) {
		getZombies(event.getMatch()).forEach(Minigamer::respawn);
		super.onStart(event);
	}

	@Override
	public void announceWinners(Match match) {
		boolean humansAlive = getHumans(match).size() > 0;

		String broadcast = "";
		if (!humansAlive)
			broadcast = "The &czombies &3have won";
		else
			if (match.getTimer().getTime() != 0)
				broadcast = "The &czombies &3has won";
			else
				broadcast = "The &9humans &3have won";

		Minigames.broadcast(broadcast + " &e" + match.getArena().getDisplayName());
	}

	// TODO: Validation on start (e.g. only two teams, one has lives, balance percentages)

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer victim = event.getMinigamer();
		Minigamer attacker = event.getAttacker();

		Match match = victim.getMatch();
		Team zombies = match.getArena().getTeams().stream()
				.filter(team -> team.getColor() == ChatColor.RED)
				.findFirst()
				.orElse(null);

		if (zombies == null) {
			Minigames.severe("Could not find zombie team on infection map, team color must be light red");
			return;
		}

		if (victim.getTeam() != zombies) {
			event.broadcastDeathMessage();
			event.setDeathMessage(null);
			victim.setTeam(zombies);
			match.broadcast(victim.getColoredName() + " has joined the " + victim.getTeam().getColoredName());
		}

		if (attacker != null)
			attacker.scored();

		super.onDeath(event);

	}

}
