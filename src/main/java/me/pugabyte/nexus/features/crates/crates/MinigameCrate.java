package me.pugabyte.nexus.features.crates.crates;

import com.destroystokyo.paper.ParticleBuilder;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.crates.models.CrateLoot;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.crates.models.VirtualCrate;
import me.pugabyte.nexus.features.crates.models.events.CrateSpawnItemEvent;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import me.pugabyte.nexus.models.perkowner.PerkOwner;
import me.pugabyte.nexus.models.perkowner.PerkOwnerService;
import me.pugabyte.nexus.utils.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class MinigameCrate extends VirtualCrate {

	PerkType perk;
	PerkOwnerService service = new PerkOwnerService();
	PerkOwner perkOwner;
	Hologram categoryHologram;
	Hologram duplicateHologram;

	@Override
	public CrateType getCrateType() {
		return CrateType.MINIGAME;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>(Collections.singletonList("&3Minigame Collectables"));
	}

	@Override
	public void pickCrateLoot() {
		List<PerkType> perks = Arrays.asList(PerkType.values());
		Map<PerkType, Double> weights = new HashMap<>();
		int maxPrice = (int) Utils.getMax(perks, PerkType::getPrice).getValue();
		int minPrice = (int) Utils.getMin(perks, PerkType::getPrice).getValue();
		perks.forEach(perkType -> weights.put(perkType, (double) (maxPrice-perkType.getPrice()+minPrice)));
		PerkType perkType = RandomUtils.getWeightedRandom(weights);
		perk = perkType;
		createFakeCrateLoot(perkType);
	}

	public void createFakeCrateLoot(PerkType perkType) {
		Perk perk = perkType.getPerk();
		ItemStack displayItem = perk.getMenuItem();
		String displayName = perk.getName();
		loot = new CrateLoot(displayName, new ArrayList<>(Collections.singletonList(displayItem)), 20, CrateType.MINIGAME, displayItem);
	}

	@Override
	public void openCrate(Location location, Player player) {
		this.perkOwner = service.get(player);
		super.openCrate(location, player);
	}

	@Override
	public void openMultiple(Location location, Player player, int amount) {
		this.perkOwner = service.get(player);
		super.openMultiple(location, player, amount);
	}

	@Override
	public CompletableFuture<Location> playAnimation(Location location) {
		CompletableFuture<Location> completableFuture = new CompletableFuture<>();
		Location newLoc = location.clone().add(0, 1.5, 0);
		AtomicInteger particles = new AtomicInteger(1);
		int taskId = Tasks.repeat(0, 1, () -> {
			for (int i = 0; i < particles.get(); i++) {
				new ParticleBuilder(Particle.ENCHANTMENT_TABLE)
						.count(1)
						.location(newLoc)
						.spawn();
			}
			particles.addAndGet(2);
		});
		Tasks.wait(TimeUtils.Time.SECOND.x(4), () -> {
			Tasks.cancel(taskId);
			completableFuture.complete(newLoc);
		});
		return completableFuture;
	}

	@Override
	public Item spawnItem(Location location, ItemStack itemStack) {
		Item item = location.getWorld().spawn(location, Item.class);
		item.setItemStack(itemStack);
		item.setGravity(false);
		item.setVelocity(new Vector(0, 0, 0));

		categoryHologram = HologramsAPI.createHologram(Nexus.getInstance(), location.clone().add(0, .3, 0));
		categoryHologram.appendTextLine("&3" + StringUtils.camelCase(perk.getPerkCategory()));
		categoryHologram.appendTextLine("&e" + StringUtils.camelCase(perk.getPerk().getName()));

		if (perkOwner.getPurchasedPerks().containsKey(perk) && perkOwner.getPurchasedPerks().get(perk)) {
			Tasks.wait(TimeUtils.Time.SECOND, () -> {
				duplicateHologram = HologramsAPI.createHologram(Nexus.getInstance(), location.clone().add(0, .5, 0));
				duplicateHologram.appendTextLine("&3Duplicate");
				duplicateHologram.appendTextLine("&f" + (perk.getPrice() / 2));
			});
		}
		new CrateSpawnItemEvent(player, loot, getCrateType()).callEvent();
		return item;
	}

	@Override
	public void giveItems() {
		if (perkOwner.getPurchasedPerks().containsKey(perk) && perkOwner.getPurchasedPerks().get(perk))
			perkOwner.setTokens(perkOwner.getTokens() + (perk.getPrice() / 2));
		else
			perkOwner.getPurchasedPerks().put(perk, true);
		service.save(perkOwner);
	}

	@Override
	public void removeItem() {
		super.removeItem();
		categoryHologram.delete();
		if (duplicateHologram != null) {
			duplicateHologram.delete();
			duplicateHologram = null;
		}
	}

	@Override
	public int getCrateAmount(Player player) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner owner = service.get(player);
		return owner.getCrates();
	}

	@Override
	public void takeCrate(Player player) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner owner = service.get(player);
		owner.setTokens(owner.getTokens() - 1);
	}

}
