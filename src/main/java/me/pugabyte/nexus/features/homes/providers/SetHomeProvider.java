package me.pugabyte.nexus.features.homes.providers;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.pugabyte.nexus.features.homes.HomesFeature;
import me.pugabyte.nexus.features.homes.HomesMenu;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.home.Home;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

public class SetHomeProvider extends MenuUtils implements InventoryProvider {
	private HomeOwner homeOwner;

	public SetHomeProvider(HomeOwner homeOwner) {
		this.homeOwner = homeOwner;
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> HomesMenu.edit(homeOwner));

		contents.set(0, 8, ClickableItem.empty(nameItem(Material.BOOK, "&eInfo", "&fChoose one of the pre-set homes to " +
				"automatically set the display item, or set your own home, and manually set the display item later")));

		contents.set(3, 4, ClickableItem.from(nameItem(
				Material.NAME_TAG,
				"&eCustom name",
				"&fNone of these names fit?||&fNo worries, you can still name it anything you'd like!"
			), e -> HomesMenu.create(homeOwner, response ->
				homeOwner.getHome(response[0]).ifPresent(HomesMenu::edit))));

		Map<String, ItemStack> options = new LinkedHashMap<String, ItemStack>() {{
			put("home", new ItemBuilder(Material.CYAN_BED)
					.loreize(false)
					.lore("&fThis is your main home. You can teleport to it with &c/h &for &c/home")
					.build());

			put("spawner", new ItemStack(Material.SPAWNER));
			put("farm", new ItemStack(Material.WHEAT));
			put("mine", new ItemBuilder(Material.DIAMOND_PICKAXE).itemFlags(ItemFlag.HIDE_ATTRIBUTES).build());
			put("storage", new ItemStack(Material.CHEST));
			put("shop", new ItemStack(Material.OAK_SIGN));

			if (player.getWorld().getEnvironment().equals(Environment.NETHER))
				put("nether", new ItemStack(Material.NETHERRACK));
			else if (player.getWorld().getEnvironment().equals(Environment.THE_END))
				put("end", new ItemStack(Material.END_STONE_BRICKS));
			else
				put("explore", new ItemStack(Material.GRASS_BLOCK));
		}};

		AtomicInteger column = new AtomicInteger(1);
		options.forEach((name, item) ->
				contents.set(1, column.getAndIncrement(), ClickableItem.from(nameItem(item, "&e" + camelCase(name)),
				e -> {
					try {
						HomesMenu.edit(addHome(name, item));
					} catch (Exception ex) {
						MenuUtils.handleException(homeOwner.getPlayer(), HomesFeature.PREFIX, ex);
					}
				})));
	}

	private Home addHome(String homeName, ItemStack itemStack) {
		Home home = Home.builder()
				.uuid(homeOwner.getUuid())
				.name(homeName)
				.location(homeOwner.getPlayer().getLocation())
				.item(itemStack)
				.build();

		homeOwner.add(home);
		new HomeService().save(homeOwner);
		return home;
	}

	@Override
	public void update(Player player, InventoryContents contents) {
	}

}