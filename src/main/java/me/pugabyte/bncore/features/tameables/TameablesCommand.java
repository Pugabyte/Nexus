package me.pugabyte.bncore.features.tameables;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.tameables.models.TameablesAction;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.OfflinePlayer;

@Aliases("tameables")
@NoArgsConstructor
public class TameablesCommand extends CustomCommand {

	TameablesCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void help() {
		reply("Correct usage: &c/tameables <info|untame|transfer [player]>");
	}

	@Path("(info|view)")
	void info() {
		BNCore.tameables.addPendingAction(player(), TameablesAction.INFO);
		reply(PREFIX + "Punch the animal you wish to view information on");
	}

	@Path("untame")
	void untame() {
		BNCore.tameables.addPendingAction(player(), TameablesAction.UNTAME);
		reply(PREFIX + "Punch the animal you wish to remove ownership of");
	}

	@Path("transfer {offlineplayer}")
	void untame(@Arg OfflinePlayer transfer) {
		BNCore.tameables.addPendingAction(player(), TameablesAction.TRANSFER.withPlayer(transfer));
		reply(PREFIX + "Punch the animal you wish to transfer to " + transfer.getName());
	}

}
