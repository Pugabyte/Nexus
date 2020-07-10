package me.pugabyte.bearnation.server.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.server.models.dumpster.Dumpster;
import me.pugabyte.bearnation.server.models.dumpster.DumpsterService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class TrashCommand extends CustomCommand implements Listener {
	private static final String TITLE = StringUtils.colorize("&4Trash");

	public TrashCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void trash() {
		player().openInventory(Bukkit.createInventory(null, 6 * 9, TITLE));
	}

	@Path("<materials...>")
	void trash(@Arg(type = Material.class) List<Material> materials) {
		DumpsterService dumpsterService = new DumpsterService();
		Dumpster dumpster = dumpsterService.get();

		for (Material material : materials) {
			dumpster.add(player().getInventory().all(material).values());
			player().getInventory().remove(material);
		}

		dumpsterService.save(dumpster);
		send(PREFIX + "Trashed all matching materials");
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!event.getView().getTitle().equals(TITLE)) return;

		DumpsterService service = new DumpsterService();
		Dumpster dumpster = service.get();

		Arrays.stream(event.getInventory().getContents())
				.filter(item -> !Utils.isNullOrAir(item))
				.forEach(dumpster::add);

		service.save(dumpster);
	}

}
