package me.pugabyte.bncore.features.commands.staff;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

@Permission("group.staff")
public class WeeeCommand extends CustomCommand {

	public WeeeCommand(CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void weee(@Arg("self") Player player) {
		List<Vector> vectors = Arrays.asList(new Vector(1, 1, 0), new Vector(-1, 1, 0), new Vector(0, 1, 1), new Vector(0, 1, -1));

		Tasks.Countdown.builder()
				.duration(Time.SECOND.x(10))
				.doZero(true)
				.onTick(i -> {
					if (i % 10 == 0)
						player.setVelocity(Utils.getRandomElement(vectors));
				})
				.start();
	}


}
