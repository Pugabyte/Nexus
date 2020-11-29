package me.pugabyte.nexus.features.minigames.mechanics;

import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.annotations.Regenerating;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchJoinEvent;
import me.pugabyte.nexus.features.minigames.models.events.matches.MatchTimerTickEvent;
import me.pugabyte.nexus.features.minigames.models.exceptions.MinigameException;
import me.pugabyte.nexus.features.minigames.models.matchdata.MastermindMatchData;
import me.pugabyte.nexus.features.minigames.models.mechanics.singleplayer.SingleplayerMechanic;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

// TODO Lobby

@Regenerating({"board", "guess"})
public final class Mastermind extends SingleplayerMechanic {

	@Override
	public String getName() {
		return "Mastermind";
	}

	@Override
	public String getDescription() {
		return "TODO";
	}

	@Override
	public ItemStack getMenuItem() {
		return new ItemStack(Material.TRIPWIRE_HOOK);
	}

	@Override
	public GameMode getGameMode() {
		return GameMode.SURVIVAL;
	}

	@Override
	public void onJoin(MatchJoinEvent event) {
		super.onJoin(event);

		Match match = event.getMatch();
		MastermindMatchData matchData = match.getMatchData();
		matchData.giveLoadout(event.getMinigamer());
		matchData.resetResultsSign(match);
	}

	public void removeBlock(Minigamer minigamer, Block block) {
		if (!canBuild(minigamer, block)) return;
		ItemStack item = new ItemStack(block.getType());
		block.setType(Material.AIR);
		Player player = minigamer.getPlayer();
		player.getInventory().addItem(item);
		player.playSound(player.getLocation(), Sound.BLOCK_STONE_BREAK, 10F, 1F);
	}

	public boolean canBuild(Minigamer minigamer, Block block) {
		return isInRegion(minigamer.getMatch(), block, "guess");
	}

	@EventHandler
	public void onTick(MatchTimerTickEvent event) {
		if (!event.getMatch().isMechanic(this)) return;

		Match match = event.getMatch();
		MastermindMatchData matchData = match.getMatchData();

		if (matchData.getGuess() > 1 && matchData.getGuess() <= 10)
			event.getMatch().getMinigamers().forEach(Minigamer::scored);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		if (EquipmentSlot.HAND != event.getHand() || block == null) return;

		Minigamer minigamer = PlayerManager.get(event.getPlayer());
		if (!minigamer.isPlaying(this)) return;

		if (Action.LEFT_CLICK_BLOCK.equals(event.getAction())) {
			event.setCancelled(true);
			removeBlock(minigamer, block);
			return;
		}

		if (Action.RIGHT_CLICK_BLOCK.equals(event.getAction())) {
			Match match = minigamer.getMatch();
			MastermindMatchData matchData = match.getMatchData();
			if (isInRegion(match, block, "button")) {
				if (!MaterialTag.BUTTONS.isTagged(block.getType()))
					return;

				// TODO Cleanup
				try {
					matchData.guess(minigamer);
				} catch (MinigameException ex) {
					minigamer.tell("&cError: " + ex.getMessage());
				} catch (Exception ex) {
					minigamer.tell("&cUnknown error occurred");
				}
				return;
			}

			if (MaterialTag.SIGNS.isTagged(block.getType())) {
				if (isInRegion(match, block, "colorblind")) {
					if (matchData.getGuess() != 1) {
						minigamer.tell("You cannot change colorblind mode in the middle of the game");
						return;
					}
					matchData.setColorblind(!matchData.isColorblind());
					matchData.giveLoadout(minigamer);
					matchData.createAnswer();
					match.getArena().regenerate();
					return;
				}
				if (isInRegion(match, block, "repeats_off")) {
					if (matchData.getGuess() != 1) {
						minigamer.tell("You cannot change the difficulty mode in the middle of the game");
						return;
					}
					matchData.setRepeats(false);
					matchData.createAnswer();
					match.getArena().regenerate();
					minigamer.tell("Difficulty mode updated");
					return;
				}
				if (isInRegion(match, block, "repeats_on")) {
					if (matchData.getGuess() != 1) {
						minigamer.tell("You cannot change the difficulty mode in the middle of the game");
						return;
					}
					matchData.setRepeats(true);
					matchData.createAnswer();
					match.getArena().regenerate();
					minigamer.tell("Difficulty mode updated");
					return;
				}
			}

			Block placed = event.getClickedBlock().getRelative(event.getBlockFace());
			if (!ItemUtils.isNullOrAir(event.getItem()) && !canBuild(minigamer, placed))
				event.setCancelled(true);
		}
	}

}
