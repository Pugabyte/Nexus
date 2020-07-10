package me.pugabyte.bearnation.minigames.features.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.ItemBuilder;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.models.Arena;
import me.pugabyte.bearnation.minigames.features.models.Team;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.BiFunction;

import static me.pugabyte.bearnation.minigames.Minigames.PREFIX;

public class PotionEffectsMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;

	public PotionEffectsMenu(@NonNull Arena arena, @NonNull Team team) {
		this.arena = arena;
		this.team = team;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(Minigames.inst(), player, text, onComplete, p -> Minigames.tasks().wait(1, () -> Minigames.getMenus().getTeamMenus().openPotionEffectsMenu(player, arena, team)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> Minigames.getMenus().getTeamMenus().openLoadoutMenu(player, arena, team));

		contents.set(0, 2, ClickableItem.from(nameItem(Material.ANVIL, "&eCopy Potions", "&3This will copy all the||&3potion effects you have||&3into the team's loadout."), e -> {
			team.getLoadout().getEffects().addAll(player.getActivePotionEffects());
			arena.write();
			Minigames.getMenus().getTeamMenus().openPotionEffectsMenu(player, arena, team);
		}));

		contents.set(0, 6, ClickableItem.from(nameItem(Material.BOOK, "&eList Potion Effects", "&3Click me to get a list of||&3all valid potion effect||&3names that can be added."), e -> {
			StringBuilder potions = new StringBuilder();
			ArrayList<PotionEffectType> potionList = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
			potionList.remove(0);
			for (PotionEffectType potion : potionList)
				potions.append(potion.getName().substring(0, 1).toUpperCase()).append(potion.getName().substring(1).toLowerCase()).append(", ");

			potions = new StringBuilder(potions.substring(0, potions.lastIndexOf(", ")));
			player.sendMessage(StringUtils.colorize(PREFIX + "&3Available Potion Effect Types:"));
			player.sendMessage(StringUtils.colorize(PREFIX + "&e" + potions));
		}));

		contents.set(0, 4, ClickableItem.from(nameItem(Material.EMERALD_BLOCK, "&eAdd Potion Effect"),
			e -> {
					PotionEffect potionEffect = new PotionEffect(PotionEffectType.SPEED, 5, 0, true, false);
					team.getLoadout().getEffects().add(potionEffect);
					arena.write();
					Minigames.getMenus().getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect);
			}));

		ItemStack deleteItem = nameItem(Material.TNT, "&cDelete Item", "&7Click me to enter deletion mode.||&7Then, click a potion effect with ||&7me to delete it.");
		contents.set(0, 8, ClickableItem.from(deleteItem, e -> Minigames.tasks().wait(2, () -> {
			if (player.getItemOnCursor().getType().equals(Material.TNT)) {
				player.setItemOnCursor(new ItemStack(Material.AIR));
			} else if (Utils.isNullOrAir(player.getItemOnCursor())) {
				player.setItemOnCursor(deleteItem);
			}
		})));

		if (team.getLoadout().getEffects() == null) return;

		int row = 1;
		int column = 0;
		for (PotionEffect potionEffect : team.getLoadout().getEffects()) {
			ItemStack item = new ItemBuilder(Material.POTION)
					.name("&e" + potionEffect.getType().getName())
					.lore("&3Duration:&e " + potionEffect.getDuration(), "&3Amplifier:&e " + (potionEffect.getAmplifier() + 1), " ", "&7Click me to edit.")
					.potionEffect(potionEffect)
					.build();

			contents.set(row, column, ClickableItem.from(item, e -> {
				if (player.getItemOnCursor().getType().equals(Material.TNT)) {
					Minigames.tasks().wait(2, () -> {
						team.getLoadout().getEffects().remove(potionEffect);
						arena.write();
						player.setItemOnCursor(new ItemStack(Material.AIR));
						Minigames.getMenus().getTeamMenus().openPotionEffectsMenu(player, arena, team);
					});
				} else {
					Minigames.getMenus().getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect);
				}
			}));

			if (column != 8) {
				column++;
			} else {
				column = 0;
				row++;
			}
		}
	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {
	}

}
