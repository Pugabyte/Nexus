package gg.projecteden.nexus.features.store.perks.autosort.commands;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.listeners.TemporaryListener;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.autosort.AutoSortUser;
import gg.projecteden.nexus.models.autosort.AutoSortUser.AutoTrashBehavior;
import gg.projecteden.nexus.models.autosort.AutoSortUserService;
import gg.projecteden.nexus.utils.ItemUtils.ItemStackComparator;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

@Permission("store.autosort")
public class AutoTrashCommand extends CustomCommand {
	private final AutoSortUserService service = new AutoSortUserService();
	private AutoSortUser user;

	public AutoTrashCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = service.get(player());
	}

	@Path("materials")
	void materials() {
		new AutoTrashMaterialEditor(user);
	}

	@Path("behavior [behavior]")
	void behavior(AutoTrashBehavior behavior) {
		if (behavior == null) {
			send("Current behavior is " + camelCase(user.getAutoTrashBehavior()));
			return;
		}

		user.setAutoTrashBehavior(behavior);
		service.save(user);
		send(PREFIX + "Auto Trash behavior set to " + camelCase(behavior));
	}

	public static class AutoTrashMaterialEditor implements TemporaryListener {
		private static final String TITLE = StringUtils.colorize("&eAuto Trash");
		private final AutoSortUser user;

		@Override
		public Player getPlayer() {
			return user.getOnlinePlayer();
		}

		public AutoTrashMaterialEditor(AutoSortUser user) {
			this.user = user;

			Inventory inv = Bukkit.createInventory(null, 6 * 9, TITLE);
			inv.setContents(user.getAutoTrashInclude().stream()
					.map(ItemStack::new)
					.sorted(new ItemStackComparator())
					.toArray(ItemStack[]::new));

			Nexus.registerTemporaryListener(this);
			user.getOnlinePlayer().openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(user.getOnlinePlayer())) return;

			Set<Material> materials = Arrays.stream(event.getInventory().getContents())
					.filter(item -> !isNullOrAir(item))
					.map(ItemStack::getType)
					.collect(Collectors.toSet());
			user.setAutoTrashInclude(materials);

			new AutoSortUserService().save(user);

			user.sendMessage(StringUtils.getPrefix("AutoTrash") + "Automatically trashing " + materials.size() + " materials");

			Nexus.unregisterTemporaryListener(this);
			event.getPlayer().closeInventory();
		}
	}
}
