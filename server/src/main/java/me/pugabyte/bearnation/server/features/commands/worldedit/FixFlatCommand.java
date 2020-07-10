package me.pugabyte.bearnation.server.features.commands.worldedit;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.World;
import lombok.SneakyThrows;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.WorldEditUtils;
import org.bukkit.Bukkit;

@DoubleSlash
public class FixFlatCommand extends CustomCommand {

	public FixFlatCommand(CommandEvent event) {
		super(event);
	}

	@SneakyThrows
	@Path
	void fixFlat() {
		World world = new BukkitWorld(player().getWorld());
		WorldEditPlugin worldEditPlugin = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
		LocalSession session = worldEditPlugin.getSession(player());
		new WorldEditUtils(player()).fixFlat(session, session.getSelection(world));
	}
}

