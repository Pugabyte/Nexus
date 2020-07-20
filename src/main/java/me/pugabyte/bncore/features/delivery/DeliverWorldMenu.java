package me.pugabyte.bncore.features.delivery;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.models.delivery.Delivery;
import me.pugabyte.bncore.models.delivery.DeliveryService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

public class DeliverWorldMenu extends MenuUtils implements InventoryProvider {
	private final DeliveryService service = new DeliveryService();
	private static List<ItemStack> items;

	public static SmartInventory getInv() {
		return SmartInventory.builder()
				.provider(new DeliverWorldMenu())
				.size(3, 9)
				.title(ChatColor.DARK_AQUA + "Choose World To Deliver To")
				.closeable(false)
				.build();
	}

	public void open(Player player, List<ItemStack> itemStacks) {
		items = itemStacks;
		getInv().open(player);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		Delivery delivery = service.get(player);
		ItemStack survival = new ItemBuilder(Material.GRASS_BLOCK).name("&3Survival").build();
		ItemStack skyblock = new ItemBuilder(Material.COBBLESTONE).name("&3Skyblock").lore("&cCurrently Disabled").build();

		contents.set(new SlotPos(1, 2), ClickableItem.from(survival, e -> {
			delivery.addToSurvival(items);
			Tasks.async(() -> {
				player.sendMessage("Size1: " + delivery.getSurvivalItems().size());
				player.sendMessage(stripColor(delivery.getSurvivalItems().toString()));
				service.deleteSync(delivery);

				player.sendMessage("\nSize1.5: " + delivery.getSurvivalItems().size());
				player.sendMessage(stripColor(delivery.getSurvivalItems().toString()));
				service.saveSync(delivery);

				Delivery delivery1 = service.get(player);
				player.sendMessage("\nSize2: " + delivery1.getSurvivalItems().size());
				player.sendMessage(stripColor(delivery1.getSurvivalItems().toString()));
			});

			getInv().close(player);
			player.sendMessage(DeliveryCommand.PREFIX + colorize("Your items have been delivered to &eSurvival"));
		}));

		contents.set(new SlotPos(1, 6), ClickableItem.empty(skyblock));

//		contents.set(new SlotPos(1, 6), ClickableItem.from(skyblock, e -> {
//			delivery.addToSkyblock(items);
//			service.delete(delivery);
//			service.save(delivery);
//
//			getInv().close(player);
//			player.sendMessage(DeliveryCommand.PREFIX + colorize("Your items have been delivered to &eSkyblock"));
//		}));
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}
}