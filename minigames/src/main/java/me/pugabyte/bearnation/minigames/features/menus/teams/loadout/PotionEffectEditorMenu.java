package me.pugabyte.bearnation.minigames.features.menus.teams.loadout;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NonNull;
import me.pugabyte.bearnation.api.utils.ItemBuilder;
import me.pugabyte.bearnation.api.utils.MenuUtils;
import me.pugabyte.bearnation.api.utils.PotionEffectEditor;
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

import java.util.function.BiFunction;

import static me.pugabyte.bearnation.minigames.Minigames.PREFIX;

public class PotionEffectEditorMenu extends MenuUtils implements InventoryProvider {
	Arena arena;
	Team team;
	PotionEffect potionEffect;

	public PotionEffectEditorMenu(@NonNull Arena arena, @NonNull Team team, @NonNull PotionEffect potionEffect) {
		this.arena = arena;
		this.team = team;
		this.potionEffect = potionEffect;
	}

	static void openAnvilMenu(Player player, Arena arena, Team team, PotionEffect potionEffect, String text, BiFunction<Player, String, AnvilGUI.Response> onComplete) {
		MenuUtils.openAnvilMenu(Minigames.inst(), player, text, onComplete, p -> Minigames.tasks().wait(1, () -> Minigames.getMenus().getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect)));
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, e -> Minigames.getMenus().getTeamMenus().openPotionEffectsMenu(player, arena, team));

		contents.set(0, 3, ClickableItem.from(nameItem(
					Material.REDSTONE,
					"&eDuration",
					"||&eCurrent value: &3" + potionEffect.getDuration()
				),
				e -> openAnvilMenu(player, arena, team, potionEffect, String.valueOf(potionEffect.getDuration()), (p, text) -> {
					if (Utils.isInt(text)) {
						team.getLoadout().getEffects().remove(potionEffect);
						potionEffect = new PotionEffectEditor(potionEffect).withDuration(Integer.parseInt(text));
						team.getLoadout().getEffects().add(potionEffect);
						arena.write();
						Minigames.tasks().wait(1, () -> {
							// Since potion effects don't have setters, pass-by-reference is broken, so we
							// have to do some hacky waits to get the menu to open with the correct object
							player.closeInventory();
							Minigames.tasks().wait(1, () -> Minigames.getMenus().getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect));
						});
						return AnvilGUI.Response.text(text);
					} else {
						player.sendMessage(StringUtils.colorize(PREFIX + "You must use an integer for the duration."));
						return AnvilGUI.Response.close();
					}
				})));

		contents.set(0, 5, ClickableItem.from(nameItem(
					Material.GLOWSTONE_DUST,
					"&eAmplifier",
					"||&eCurrent value: &3" + (potionEffect.getAmplifier() + 1)
				),
				e -> openAnvilMenu(player, arena, team, potionEffect, String.valueOf(potionEffect.getAmplifier()), (p, text) -> {
					if (Utils.isInt(text)) {
						team.getLoadout().getEffects().remove(potionEffect);
						potionEffect = new PotionEffectEditor(potionEffect).withAmplifier(Integer.parseInt(text) - 1);
						team.getLoadout().getEffects().add(potionEffect);
						arena.write();
						Minigames.tasks().wait(1, () -> {
							player.closeInventory();
							Minigames.tasks().wait(1, () -> Minigames.getMenus().getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect));
						});
						return AnvilGUI.Response.text(text);
					} else {
						player.sendMessage(StringUtils.colorize(PREFIX + "You must use an integer for the amplifier."));
						return AnvilGUI.Response.close();
					}
				})));

		contents.set(0, 8, ClickableItem.from(nameItem(Material.END_CRYSTAL, "&eSave"), e-> arena.write()));

		int row = 2;
		int column = 0;
		for(PotionEffectType effect : PotionEffectType.values()){
			if(effect == null) continue;

			ItemStack potionItem = new ItemBuilder(Material.POTION)
					.name("&e" + StringUtils.camelCase(effect.getName().replace("_", " ")))
					.potionEffect(new PotionEffect(effect, 5 ,0))
					.potionEffectColor(effect.getColor())
					.build();

			if(effect == potionEffect.getType()) potionItem.setType(Material.SPLASH_POTION);

			contents.set(row, column, ClickableItem.from(potionItem,
					e-> {
						team.getLoadout().getEffects().remove(potionEffect);
						potionEffect = new PotionEffectEditor(potionEffect).withType(effect);
						team.getLoadout().getEffects().add(potionEffect);
						arena.write();
						Minigames.tasks().wait(1, () -> {
							player.closeInventory();
							Minigames.tasks().wait(1, () -> Minigames.getMenus().getTeamMenus().openPotionEffectEditorMenu(player, arena, team, potionEffect));
						});
					}));

			if(column == 8){
				column = 0;
				row++;
			} else {
				column++;
			}
		}

	}

	@Override
	public void update(Player player, InventoryContents inventoryContents) {

	}

}
