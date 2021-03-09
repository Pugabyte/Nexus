package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.shops.ShopUtils;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class EditProductProvider extends _ShopProvider {
	private final Product product;

	public EditProductProvider(_ShopProvider previousMenu, Product product) {
		this.previousMenu = previousMenu;
		this.product = product;
		this.rows = 4;
	}

	@Override
	public void open(Player viewer, int page) {
		open(viewer, page, this, "&0Edit Item");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		contents.set(0, 4, ClickableItem.from(product.getOwnLore(), e -> new ExchangeConfigProvider(this, product).open(player)));
		contents.set(1, 3, ClickableItem.from(nameItem(Material.LIME_CONCRETE_POWDER, "&6Add Stock"), e -> new AddStockProvider(this, product).open(player)));
		contents.set(1, 5, ClickableItem.from(nameItem(Material.RED_CONCRETE_POWDER, "&6Remove Stock"), e -> new RemoveStockProvider(this, product).open(player)));
		contents.set(3, 4, ClickableItem.from(new ItemBuilder(Material.LAVA_BUCKET).name("&cDelete").build(), e ->
				ConfirmationMenu.builder()
						.onConfirm(e2 -> {
							Shop shop = service.get(player);
							shop.getProducts().remove(product);
							ShopUtils.giveItems(player, product.getItemStacks());
							service.save(shop);
							previousMenu.open(player);
						})
						.onCancel(e2 -> open(player))
						.open(player)));

	}

	public static class AddStockProvider implements Listener {
		private final static String TITLE = colorize("&0Add Stock");
		private Player player;
		private final _ShopProvider previousMenu;
		private final Product product;

		public AddStockProvider(_ShopProvider previousMenu, Product product) {
			this.previousMenu = previousMenu;
			this.product = product;
		}

		public void open(Player player) {
			this.player = player;
			product.setEditing(true);

			Inventory inv = Bukkit.createInventory(null, 54, TITLE);
			Nexus.registerListener(this);
			player.openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(player)) return;

			ItemStack[] contents = event.getInventory().getContents();
			for (ItemStack content : contents) {
				if (ItemUtils.isNullOrAir(content))
					continue;

				if (product.getItem().isSimilar(content))
					product.addStock(content.getAmount());
				else
					PlayerUtils.giveItem(player, content);
			}

			new ShopService().save(product.getShop());
			product.setEditing(false);

			Nexus.unregisterListener(this);
			event.getPlayer().closeInventory();
			Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

	public static class RemoveStockProvider implements Listener {
		private final static String TITLE = colorize("&0Remove Stock");
		private Player player;
		private final _ShopProvider previousMenu;
		private final Product product;
		private int itemsAdded;

		public RemoveStockProvider(_ShopProvider previousMenu, Product product) {
			this.previousMenu = previousMenu;
			this.product = product;
		}

		public void open(Player player) {
			this.player = player;
			product.setEditing(true);

			final int size = 54;
			Inventory inv = Bukkit.createInventory(null, size, TITLE);

			List<ItemStack> items = product.getItemStacks(size);

			for (ItemStack item : items)
				itemsAdded += item.getAmount();

			inv.setContents(items.toArray(new ItemStack[0]));
			Nexus.registerListener(this);
			player.openInventory(inv);
		}

		@EventHandler
		public void onChestClose(InventoryCloseEvent event) {
			if (event.getInventory().getHolder() != null) return;
			if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;
			if (!event.getPlayer().equals(player)) return;

			ItemStack[] contents = event.getInventory().getContents();
			int itemsLeft = 0;
			for (ItemStack content : contents) {
				if (ItemUtils.isNullOrAir(content))
					continue;

				if (!product.getItem().isSimilar(content)) {
					PlayerUtils.giveItem(player, content);
					continue;
				}

				itemsLeft += content.getAmount();
			}

			product.removeStock(itemsAdded - itemsLeft);
			new ShopService().save(product.getShop());
			product.setEditing(false);

			Nexus.unregisterListener(this);
			event.getPlayer().closeInventory();
			Tasks.wait(1, () -> previousMenu.open(player));
		}
	}

}
