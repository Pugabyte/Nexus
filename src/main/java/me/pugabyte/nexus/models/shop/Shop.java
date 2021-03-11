package me.pugabyte.nexus.models.shop;

import com.mongodb.DBObject;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.PostLoad;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.EnumUtils.IteratableEnum;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SerializationUtils.JSON;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static me.pugabyte.nexus.features.shops.ShopUtils.giveItems;
import static me.pugabyte.nexus.features.shops.ShopUtils.prettyMoney;
import static me.pugabyte.nexus.features.shops.Shops.PREFIX;
import static me.pugabyte.nexus.utils.ItemUtils.getShulkerContents;
import static me.pugabyte.nexus.utils.StringUtils.pretty;
import static me.pugabyte.nexus.utils.StringUtils.stripColor;

@Data
@Builder
@Entity("shop")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Shop extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<String> description = new ArrayList<>();
	@Embedded
	private List<Product> products = new ArrayList<>();
	@Embedded
	private List<ItemStack> holding = new ArrayList<>();
	// TODO holding for money, maybe? would make withdrawing money more complicated
	// private double profit;

	public List<Product> getProducts(ShopGroup shopGroup) {
		return products.stream().filter(product -> product.getShopGroup().equals(shopGroup)).collect(Collectors.toList());
	}

	public List<String> getDescription() {
		return description.stream().filter(line -> !isNullOrEmpty(line)).collect(Collectors.toList());
	}

	public void setDescription(List<String> description) {
		this.description = new ArrayList<String>() {{
			for (String line : description)
				if (!isNullOrEmpty(stripColor(line).replace(StringUtils.getColorChar(), "")))
					add(line.startsWith("&") ? line : "&f" + line);
		}};
	}

	public boolean isMarket() {
		return uuid.equals(Nexus.getUUID0());
	}

	public String[] getDescriptionArray() {
		return description.isEmpty() ? new String[]{"", "", "", ""} : description.stream().map(StringUtils::decolorize).toArray(String[]::new);
	}

	public List<Product> getInStock(ShopGroup shopGroup) {
		return getProducts(shopGroup).stream().filter(product -> product.getExchange().canFulfillPurchase()).collect(Collectors.toList());
	}

	public List<Product> getOutOfStock(ShopGroup shopGroup) {
		return getProducts(shopGroup).stream().filter(product -> !product.getExchange().canFulfillPurchase()).collect(Collectors.toList());
	}

	public void addHolding(List<ItemStack> itemStacks) {
		itemStacks.forEach(this::addHolding);
	}

	public void addHolding(ItemStack itemStack) {
		ItemUtils.combine(holding, itemStack.clone());
	}

	public enum ShopGroup {
		SURVIVAL,
		RESOURCE,
		SKYBLOCK,
		ONEBLOCK;

		public static ShopGroup get(org.bukkit.entity.Entity entity) {
			return get(entity.getWorld());
		}

		public static ShopGroup get(World world) {
			return get(world.getName());
		}

		public static ShopGroup get(String world) {
			if (world.toLowerCase().startsWith("resource"))
				return RESOURCE;
			else if (WorldGroup.get(world) == WorldGroup.SKYBLOCK)
				return SKYBLOCK;
			else if (WorldGroup.get(world) == WorldGroup.ONEBLOCK)
				return ONEBLOCK;
			else if (WorldGroup.get(world) == WorldGroup.SURVIVAL)
				return SURVIVAL;

			return null;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	@Converters({UUIDConverter.class, ItemStackConverter.class})
	public static class Product {
		@NonNull
		private UUID uuid;
		@NonNull
		private ShopGroup shopGroup;
		@Embedded
		private ItemStack item;
		private double stock;
		private ExchangeType exchangeType;
		private Object price;
		private boolean enabled = true;

		private transient boolean editing;

		public Product(@NonNull UUID uuid, @NonNull ShopGroup shopGroup, ItemStack item, double stock, ExchangeType exchangeType, Object price) {
			this.uuid = uuid;
			this.shopGroup = shopGroup;
			this.item = item;
			this.stock = stock;
			this.exchangeType = exchangeType;
			this.price = price;
		}

		@PostLoad
		void fix(DBObject dbObject) {
			if (!(price instanceof Number))
				price = JSON.deserializeItemStack((Map<String, Object>) dbObject.get("price"));
		}

		public Shop getShop() {
			return new ShopService().get(uuid);
		}

		public ItemStack getItem() {
			return item.clone();
		}

		public void addStock(int amount) {
			setStock(stock + amount);
		}

		public void removeStock(int amount) {
			setStock(stock - amount);
		}

		public void setStock(double stock) {
			if (isMarket())
				return;

			if (exchangeType == ExchangeType.BUY && stock < 0)
				this.stock = -1;
			else
				this.stock = Math.max(stock, 0);
		}

		public double getCalculatedStock() {
			if (exchangeType == ExchangeType.BUY && stock == -1)
				return Nexus.getEcon().getBalance(getShop().getOfflinePlayer());
			else
				return stock;
		}

		@SneakyThrows
		public void process(Player customer) {
			if (uuid.equals(customer.getUniqueId()))
				throw new InvalidInputException("You cannot buy items from yourself");

			if (editing)
				throw new InvalidInputException("You cannot buy this item right now, it is being edited by the shop owner");

			getExchange().process(customer);
			log(customer);
		}

		public void log(Player customer) {
			List<String> columns = new ArrayList<>(Arrays.asList(
					DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now()),
					getUuid().toString(),
					getShop().getOfflinePlayer().getName(),
					customer.getUniqueId().toString(),
					customer.getName(),
					getShopGroup().name(),
					item.getType().name(),
					String.valueOf(item.getAmount()),
					exchangeType.name()
			));

			if (price instanceof ItemStack) {
				columns.add(((ItemStack) price).getType().name());
				columns.add(String.valueOf(((ItemStack) price).getAmount()));
			} else {
				columns.add(String.valueOf(price));
				columns.add("");
			}

			Nexus.csvLog("exchange", String.join(",", columns));
		}

		@NotNull
		public Exchange getExchange() {
			return exchangeType.init(this);
		}

		public ItemBuilder getItemWithLore() {
			ItemBuilder builder = new ItemBuilder(item).lore("&f");

			if (item.getType() != Material.ENCHANTED_BOOK)
				builder.itemFlags(ItemFlag.HIDE_ATTRIBUTES);

			if (!getShulkerContents(item).isEmpty())
				builder.lore("&7Right click to view contents").lore("&f");

			return builder;
		}

		public ItemBuilder getItemWithCustomerLore() {
			return getItemWithLore().lore(getExchange().getLore());
		}

		public ItemBuilder getItemWithOwnLore() {
			ItemBuilder item = getItemWithLore();
			if (!enabled)
				item.lore("&cDisabled");

			return item
					.lore(getExchange().getOwnLore())
					.lore("", "&7Click to edit");
		}

		public boolean isMarket() {
			return getShop().isMarket();
		}

		public List<ItemStack> getItemStacks() {
			return getItemStacks(-1);
		}

		public List<ItemStack> getItemStacks(int maxStacks) {
			List<ItemStack> items = new ArrayList<>();

			ItemStack item = this.item.clone();
			double stock = this.stock;
			int maxStackSize = item.getMaxStackSize();

			while (stock > 0) {
				if (maxStacks > 0 && items.size() > maxStacks)
					break;

				ItemStack next = new ItemStack(item.clone());
				next.setAmount((int) Math.min(maxStackSize, stock));
				stock -= next.getAmount();
				items.add(next);
			}

			return items;
		}

	}

	// Dumb enum due to morphia refusing to deserialize interfaces properly
	public enum ExchangeType implements IteratableEnum {
		SELL(SellExchange.class),
		TRADE(TradeExchange.class),
		BUY(BuyExchange.class);

		@Getter
		private final Class<? extends Exchange> clazz;

		ExchangeType(Class<? extends Exchange> clazz) {
			this.clazz = clazz;
		}

		@SneakyThrows
		public Exchange init(Product product) {
			return (Exchange) clazz.getDeclaredConstructors()[0].newInstance(product);
		}
	}

	public interface Exchange {

		Product getProduct();

		void process(Player customer);
		boolean canFulfillPurchase();

		List<String> getLore();
		List<String> getOwnLore();

		default void checkStock() {
			if (!getProduct().isMarket()) {
				if (getProduct().getCalculatedStock() <= 0)
					throw new InvalidInputException("This item is out of stock");
				if (!canFulfillPurchase())
					throw new InvalidInputException("There is not enough stock to fulfill your purchase");
			}
		}
	}

	@Data
	// Customer buying an item from the shop owner for money
	public static class SellExchange implements Exchange {
		@NonNull
		private final Product product;
		private final double price;

		public SellExchange(@NonNull Product product) {
			this.product = product;
			this.price = (double) product.getPrice();
		}

		@Override
		public void process(Player customer) {
			checkStock();

			if (!Nexus.getEcon().has(customer, price))
				throw new InvalidInputException("You do not have enough money to purchase this item");

			product.setStock(product.getStock() - product.getItem().getAmount());
			if (price > 0) {
				Nexus.getEcon().withdrawPlayer(customer, price);
				if (!product.isMarket())
					Nexus.getEcon().depositPlayer(product.getShop().getOfflinePlayer(), price);
			}
			giveItems(customer, product.getItem());
			new ShopService().save(product.getShop());
			PlayerUtils.send(customer, PREFIX + "You purchased " + pretty(product.getItem()) + " for " + prettyMoney(price));
		}

		public boolean canFulfillPurchase() {
			return product.getCalculatedStock() >= product.getItem().getAmount();
		}

		@Override
		public List<String> getLore() {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price);

			if (product.getUuid().equals(Nexus.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock,
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore() {
			int stock = (int) product.getStock();
			return Arrays.asList(
					"&7Selling &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price),
					"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock
			);
		}
	}

	@Data
	// Customer buying an item from the shop owner for other items
	public static class TradeExchange implements Exchange {
		@NonNull
		private Product product;
		private final ItemStack price;

		public TradeExchange(@NonNull Product product) {
			this.product = product;
			this.price = (ItemStack) product.getPrice();
		}

		@Override
		public void process(Player customer) {
			checkStock();

			if (!customer.getInventory().containsAtLeast(price, price.getAmount()))
				throw new InvalidInputException("You do not have " + pretty(price) + " to purchase this item");

			product.setStock(product.getStock() - product.getItem().getAmount());
			customer.getInventory().removeItem(price);
			if (!product.isMarket())
				product.getShop().addHolding(price);
			giveItems(customer, product.getItem());
			new ShopService().save(product.getShop());
			PlayerUtils.send(customer, PREFIX + "You purchased " + pretty(product.getItem()) + " for " + pretty(price));
		}

		@Override
		public boolean canFulfillPurchase() {
			return product.getCalculatedStock() >= product.getItem().getAmount();
		}

		@Override
		public List<String> getLore() {
			int stock = (int) product.getStock();
			String desc = "&7Buy &e" + product.getItem().getAmount() + " &7for &a" + pretty(price);
			if (product.getUuid().equals(Nexus.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock,
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore() {
			int stock = (int) product.getStock();
			return Arrays.asList(
					"&7Selling &e" + product.getItem().getAmount() + " &7for &a" + pretty(price),
					"&7Stock: " + (stock > 0 ? "&e" : "&c") + stock
			);
		}
	}

	@Data
	// Customer selling an item to the shop owner for money
	public static class BuyExchange implements Exchange {
		@NonNull
		private final Product product;
		private final Double price;

		public BuyExchange(@NonNull Product product) {
			this.product = product;
			this.price = (Double) product.getPrice();
		}

		@Override
		public void process(Player customer) {
			checkStock();

			if (!customer.getInventory().containsAtLeast(product.getItem(), product.getItem().getAmount()))
				throw new InvalidInputException("You do not have " + pretty(product.getItem()) + " to sell");

			product.setStock(product.getStock() - price);
			if (price > 0) {
				if (!product.isMarket())
					Nexus.getEcon().withdrawPlayer(product.getShop().getOfflinePlayer(), price);
				Nexus.getEcon().depositPlayer(customer, price);
			}
			customer.getInventory().removeItem(product.getItem());
			if (!product.isMarket())
				product.getShop().addHolding(product.getItem());
			new ShopService().save(product.getShop());
			PlayerUtils.send(customer, PREFIX + "You sold " + pretty(product.getItem()) + " for " + prettyMoney(price));
		}

		@Override
		public boolean canFulfillPurchase() {
			return product.getCalculatedStock() >= price;
		}

		@Override
		public List<String> getLore() {
			String desc = "&7Sell &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price);
			if (product.getUuid().equals(Nexus.getUUID0()))
				return Arrays.asList(
						desc,
						"&7Seller: &6Market"
				);
			else
				return Arrays.asList(
						desc,
						"&7Stock: &e" + prettyMoney(product.getCalculatedStock(), false),
						"&7Seller: &e" + product.getShop().getOfflinePlayer().getName()
				);
		}

		@Override
		public List<String> getOwnLore() {
			return Arrays.asList(
					"&7Buying &e" + product.getItem().getAmount() + " &7for &a" + prettyMoney(price),
					"&7Stock: &e" + prettyMoney(product.getCalculatedStock(), false)
			);
		}

	}

}
