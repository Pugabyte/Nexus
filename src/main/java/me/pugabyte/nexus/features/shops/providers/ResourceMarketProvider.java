package me.pugabyte.nexus.features.shops.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import me.pugabyte.nexus.models.shop.Shop;
import me.pugabyte.nexus.models.shop.Shop.Product;
import me.pugabyte.nexus.models.shop.ShopService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ResourceMarketProvider extends _ShopProvider {
	private static final ShopService service = new ShopService();
	private static final Shop market = service.getMarket();

	public ResourceMarketProvider(_ShopProvider previousMenu) {
		this.previousMenu = previousMenu;
	}

	@Override
	public void open(Player player, int page) {
		open(player, page, this, "Resource World Market");
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		super.init(player, contents);

		final Shop shop = service.get(player);
		final List<Product> products = market.getProducts().stream().filter(Product::isResourceWorld).toList();

		final ItemBuilder info = new ItemBuilder(Material.BOOK).name("&6&lInfo")
			.lore("Items are automatically sold when collected");
		contents.set(0, 4, ClickableItem.empty(info.build()));

		List<ClickableItem> items = new ArrayList<>();

		products.forEach(product -> {
			final Material type = product.getItem().getType();
			final ItemBuilder builder = product.getItemWithLore();
			final Runnable toggle;

			if (shop.getDisabledMarketItems().contains(type)) {
				builder.lore("&cDisabled").lore("&aClick to enable");
				toggle = () -> shop.getDisabledMarketItems().remove(type);
			} else {
				builder.lore("&aEnabled").lore("&cClick to disable");
				toggle = () -> shop.getDisabledMarketItems().add(type);
			}

			final ItemStack item = builder.build();
			items.add(ClickableItem.from(item, e -> {
				toggle.run();
				open(player, contents.pagination().getPage());
			}));
		});

		addPagination(player, contents, items);
	}

}
