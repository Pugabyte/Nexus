package me.pugabyte.nexus.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.interactioncommand.InteractionCommand;
import me.pugabyte.nexus.models.interactioncommand.InteractionCommandService;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Permission("group.staff")
@Aliases({"cmds", "cmdsign"})
public class InteractionCommandsCommand extends CustomCommand implements Listener {
	InteractionCommandService service = new InteractionCommandService();
	Block target;
	Map<Integer, InteractionCommand> commands;

	public InteractionCommandsCommand(@NonNull CommandEvent event) {
		super(event);
		target = player().getTargetBlockExact(20);
		if (target != null)
			commands = service.get(target.getLocation());
	}

	@Path("<index> <command...>")
	void set(int index, String command) {
		if (index < 1)
			error("Index cannot be less than 1");
		service.save(new InteractionCommand(target.getLocation(), index, command));
		send(PREFIX + "Set command at index &e" + index + " &3to &e" + command);
	}

	@Path("(delete|remove|clear) [index]")
	void delete(Integer index) {
		if (commands == null || commands.isEmpty())
			error("There are no commands present at that location");

		if (index != null) {
			InteractionCommand command = commands.get(index);
			if (command == null)
				error("There are no commands present at that index");
			service.delete(command);
			send(PREFIX + "Deleted command &e" + command.getCommand() + " &3at index " + command.getIndex());
		} else {
			service.delete(target.getLocation());
			send(PREFIX + "Deleted &e" + commands.size() + " &3commands at that location");
		}
	}

	@Path("read")
	void read() {
		if (commands == null || commands.isEmpty())
			error("There are no commands present at that location");
		line();
		send(PREFIX + "Commands:");
		commands.forEach((index, command) -> send("&e" + index + " &7" + command.getCommand()));
	}

	@Path("clearCache")
	void clearCache() {
		service.initialize();
		send("Cache cleared");
	}

//	@Path("copy")
//	void copy() {
//
//	}
//
//	@Path("paste")
//	void paste() {
//
//	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		if (event.getAction() != Action.PHYSICAL && MaterialTag.PRESSURE_PLATES.isTagged(event.getClickedBlock().getType())) return;
		if (event.getAction() == Action.PHYSICAL && Utils.isVanished(event.getPlayer())) return;

		Map<Integer, InteractionCommand> commands = new InteractionCommandService().get(event.getClickedBlock().getLocation());
		if (commands == null || commands.isEmpty()) return;

		AtomicInteger wait = new AtomicInteger(0);
		commands.forEach((index, command) ->
				Tasks.wait(wait.getAndAdd(3), () ->
						command.run(event)));
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (new InteractionCommandService().delete(event.getBlock().getLocation()))
			send(event.getPlayer(), PREFIX + "Cleared");
	}
}