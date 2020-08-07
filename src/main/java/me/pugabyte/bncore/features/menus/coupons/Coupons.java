package me.pugabyte.bncore.features.menus.coupons;

import lombok.Getter;
import me.pugabyte.bncore.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Coupons {

	public Coupons() {
		registerCoupons();
	}

	private void registerCoupons() {
		new CouponBuilder(anySong, event -> event.getPlayer().sendMessage("ur a song nerd lol"));
		new CouponBuilder(wingStyle, event -> event.getPlayer().sendMessage("ur a wing nerd"));
		new CouponBuilder(particle, event -> event.getPlayer().sendMessage("ur a particle nerd"));
	}

	@Getter
	public final ItemStack anySong = new ItemBuilder(Material.PAPER).glow().name("&eCoupon for any song")
			.lore(" ").lore("&3Right click me to").lore("&3redeem any &epowder song").build();

	@Getter
	public final ItemStack wingStyle = new ItemBuilder(Material.PAPER).glow().name("&eCoupon for any wing style")
			.lore(" ").lore("&3Right click me to").lore("&3redeem any &ewing style").build();

	@Getter
	public final ItemStack particle = new ItemBuilder(Material.PAPER).glow().name("&eCoupon for any particle effect")
			.lore(" ").lore("&3Right click me to").lore("&3redeem any &eparticle effect").build();
}