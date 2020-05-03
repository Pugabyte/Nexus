package me.pugabyte.bncore.features.commands;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.BlockUtils;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Camaros
 */

@Aliases("sws")
@NoArgsConstructor
public class SidewaysStairsCommand extends CustomCommand implements Listener {
	public static final String PREFIX = StringUtils.getPrefix("SidewaysStairs");
	static Map<Player, SidewaysStairsPlayer> playerData = new HashMap<>();

	private static final List<String> validAngles = Arrays.asList("north",  "east", "south", "west");

	private SidewaysStairsPlayer swsPlayer;

	SidewaysStairsCommand(CommandEvent event) {
		super(event);
		if (!playerData.containsKey(player()))
			playerData.put(player(), new SidewaysStairsPlayer(player()));

		swsPlayer = playerData.get(player());
	}

	@Path
	void help() {
		send(PREFIX + "Commands:");
		send("&c/sws toggle &7- Turn SWS on or off");
		send("&c/sws set <north|south|east|west> &7- Set the direction of the stairs to be placed.");
		send("&c/sws setupsidedown <true|false> &7- Toggle upsidedown stairs in set placement mode.");
		send("&c/sws rotate &7- Rotate the stair angle.");
		send("&c/sws copy &7- Copy the angle of an existing stair.");
		send("&c/sws upsidedown [true|false] &7- Toggle whether stairs can be placed upsidedown or not.");
	}

	@Path("toggle")
	void toggle() {
		toggle(!swsPlayer.isEnabled());
	}

	@Path("<true|false>")
	void toggle(boolean enable) {
		swsPlayer.setEnabled(enable);
		send(PREFIX + (swsPlayer.isEnabled() ? "Enabled" : "Disabled"));
	}

	@Path("(set|angle|setangle) <north|south|east|west>")
	void setAngle(String angle) {
		if (angle == null)
			send(PREFIX + "/sws angle <north/south/east/west> -&7 Set the angle to place stair blocks.");
		else if (validAngles.contains(angle.toLowerCase())) {
			swsPlayer.setEnabled(true);
			swsPlayer.setAction(SwsAction.SET_ANGLE);
			swsPlayer.setDirection(angle.toLowerCase());
			send(PREFIX + "Angle successfully set to " + angle);
		} else
			send(PREFIX + "Invalid angle. Angle must be North, South, East, or West");
	}

	@Path("(setupsidedown) [true/false]")
	void setUpsidedown(Boolean value){
		if (value == null)
			setUpsidedown(!swsPlayer.getHalf().equals("top"));
		else {
			swsPlayer.setEnabled(true);
			swsPlayer.setAction(SwsAction.SET_ANGLE);
			swsPlayer.setHalf(value ? "top" : "bottom");
			send(PREFIX + String.format("Upsidedown stairs has now been %s.", value ? "enabled" : "disabled"));
		}
	}

	@Path("copy")
	void copy() {
		swsPlayer.setEnabled(true);
		swsPlayer.setAction(SwsAction.COPY);
		send(PREFIX + "Right click a stair block to copy its angle.");
	}

	@Path("rotate")
	void rotate() {
		swsPlayer.setEnabled(true);
		swsPlayer.setAction(SwsAction.SET_ANGLE);
		Iterator<String> i = validAngles.iterator();
		while(i.hasNext())
			if(i.next().equals(swsPlayer.direction))
				if(i.hasNext())
					swsPlayer.setDirection(i.next());
				else
					swsPlayer.setDirection(validAngles.get(0));
		send(PREFIX + String.format("Angle changed to %s.", swsPlayer.getDirection()));
	}

	@Path("upsideDown")
	void upsideDown() {
		swsPlayer.setEnabled(true);
		upsideDown(swsPlayer.getAction() == SwsAction.DISABLE_UPSIDEDOWN_PLACEMENT);
	}

	@Path("upsideDown <true|false>")
	void upsideDown(boolean allow) {
		swsPlayer.setEnabled(true);
		swsPlayer.setAction(allow ? SwsAction.NONE : SwsAction.DISABLE_UPSIDEDOWN_PLACEMENT);
		send(PREFIX + String.format("Upsidedown stair placement  %s.",  allow ? "enabled" : "disabled"));
	}

	@EventHandler
	public void onStairInteract(PlayerInteractEvent event) {
		if (event.getHand() == null) return;
		if (event.getHand().equals(EquipmentSlot.HAND)) return;
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {

			Block block = event.getClickedBlock();
			Player player = event.getPlayer();
			SidewaysStairsPlayer swsPlayer = playerData.get(player);


			if (swsPlayer != null && swsPlayer.isEnabled())
				if (swsPlayer.getAction() == SwsAction.COPY)
					if (MaterialTag.STAIRS.isTagged(block.getType())) {
						swsPlayer.setAction(SwsAction.SET_ANGLE);
						String direction = BlockUtils.getBlockProperty(block, "facing");
						String half = BlockUtils.getBlockProperty(block, "half");
						swsPlayer.setDirection(direction);
						swsPlayer.setHalf(half);
						swsPlayer.setEnabled(true);
						player.sendMessage(PREFIX + "Angle succesfully copied (" + direction + (!"bottom".equals(half) ? "/upsidedown" : "") + ").");
					} else {
						player.sendMessage(PREFIX + "Can only copy angle of a stair block.");
					}
		}
	}

	@EventHandler
	public void onStairPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Block block = event.getBlock();
		SidewaysStairsPlayer swsPlayer = playerData.get(player);

		if (MaterialTag.STAIRS.isTagged(block.getType()))
			if (swsPlayer != null && swsPlayer.isEnabled())
				if (swsPlayer.getAction() == SwsAction.SET_ANGLE) {
					BlockUtils.updateBlockProperty(block, "facing", swsPlayer.direction);
					BlockUtils.updateBlockProperty(block, "half", swsPlayer.half);
				} else if (swsPlayer.getAction() == SwsAction.DISABLE_UPSIDEDOWN_PLACEMENT) {
					BlockUtils.updateBlockProperty(block, "half", "bottom");
				}
	}

	enum SwsAction{
		NONE, COPY, SET_ANGLE, DISABLE_UPSIDEDOWN_PLACEMENT;
	}

	@Data
	@RequiredArgsConstructor
	public class SidewaysStairsPlayer {
		@NonNull
		private final Player player;
		private boolean enabled = false;
		private SwsAction action = SwsAction.NONE;
		private String direction = "north";
		private String half = "bottom";
	}

}
