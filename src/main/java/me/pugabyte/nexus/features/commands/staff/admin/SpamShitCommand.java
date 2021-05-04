package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Permission("rank.owner")
public class SpamShitCommand extends CustomCommand implements Listener {
	private static boolean spamming = false;
	private static Map<Material, Integer> taskIds = new HashMap<>();
	private static Map<Material, Class<? extends Projectile>> projectiles = new HashMap<>() {{
		put(Material.EGG, Egg.class);
		put(Material.STICK, Arrow.class);
		put(Material.SNOWBALL, Snowball.class);
		put(Material.FIRE_CHARGE, Fireball.class);
	}};

	public SpamShitCommand(@NonNull CommandEvent event) {
		super(event);
	}

	boolean isPug(Player player) {
		return player.getUniqueId().equals(PlayerUtils.getPlayer("Pugabyte").getUniqueId());
	}

	@Path
	void run() {
		if (!isPug(player()))
			throw new InvalidInputException("You cannot run this command");

		spamming = !spamming;
		send("Spamming shit turned " + (spamming ? "on" : "off"));

		if (!spamming) {
			taskIds.forEach((material, taskId) -> Tasks.cancel(taskId));
			taskIds.clear();
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!spamming)
			return;

		Player player = event.getPlayer();
		if (!isPug(player))
			return;

		if (!ActionGroup.RIGHT_CLICK.applies(event))
			return;

		Material material = player.getInventory().getItemInMainHand().getType();
		if (!projectiles.containsKey(material))
			return;

		event.setCancelled(true);

		if (taskIds.containsKey(material)) {
			Tasks.cancel(taskIds.get(material));
			taskIds.remove(material);
		} else {
			Class<? extends Projectile> projectile = projectiles.get(material);
			int taskId = Tasks.repeat(0, 1, () -> {
				Vector vector = player.getLocation().getDirection();
				if (projectile == Arrow.class) vector.multiply(20);
				player.launchProjectile(projectile, vector);
			});
			taskIds.put(material, taskId);
		}
	}

}
