package me.pugabyte.bearnation.minigames.features.mechanics.common;

import me.pugabyte.bearnation.api.utils.MaterialTag;
import me.pugabyte.bearnation.minigames.features.managers.PlayerManager;
import me.pugabyte.bearnation.minigames.features.models.Minigamer;
import me.pugabyte.bearnation.minigames.features.models.events.matches.minigamers.MinigamerDeathEvent;
import me.pugabyte.bearnation.minigames.features.models.matchdata.CaptureTheFlagMatchData;
import me.pugabyte.bearnation.minigames.features.models.matchdata.Flag;
import me.pugabyte.bearnation.minigames.features.models.mechanics.multiplayer.teams.BalancedTeamMechanic;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public abstract class CaptureTheFlagMechanic extends BalancedTeamMechanic {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Minigamer minigamer = PlayerManager.get(event.getPlayer());

		if (!(
				minigamer.isPlaying(this) &&
						event.getAction() == Action.RIGHT_CLICK_BLOCK &&
						event.getClickedBlock() != null &&
						event.getHand() != null &&
						event.getHand().equals(EquipmentSlot.HAND) &&
						minigamer.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR &&
						MaterialTag.SIGNS.isTagged(event.getClickedBlock().getType())
		)) return;

		Sign sign = (Sign) event.getClickedBlock().getState();

		if ((ChatColor.DARK_BLUE + "[Minigame]").equalsIgnoreCase(sign.getLine(0))) {
			if ((ChatColor.GREEN + "Flag").equalsIgnoreCase(sign.getLine(1))) {
				onFlagInteract(minigamer, sign);
			}
		}
	}

	public abstract void onFlagInteract(Minigamer minigamer, Sign sign);

	@Override
	public void onDeath(MinigamerDeathEvent event) {
		Minigamer minigamer = event.getMinigamer();
		CaptureTheFlagMatchData matchData = minigamer.getMatch().getMatchData();
		Flag carriedFlag = matchData.getFlagByCarrier(minigamer);
		if (carriedFlag != null) {
			carriedFlag.drop(minigamer.getPlayer().getLocation());

			matchData.removeFlagCarrier(minigamer);
			event.getMatch().broadcast(minigamer.getColoredName() + " &3dropped " + carriedFlag.getTeam().getColoredName() + "&3's flag");
		}

		super.onDeath(event);
	}

}
