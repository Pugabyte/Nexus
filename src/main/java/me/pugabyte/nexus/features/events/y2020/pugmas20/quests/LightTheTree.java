package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.QuestNPC;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ActionBarUtils;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatType;
import me.pugabyte.nexus.utils.StringUtils.TimespanFormatter;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Tasks.Countdown;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.WorldEditUtils.Paste;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.PREFIX;
import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isAtPugmas;

@NoArgsConstructor
public class LightTheTree implements Listener {
	private static final String lighterRg = Pugmas20.getRegion() + "_lighter";
	//
	public static final ItemStack lighter_broken = Pugmas20.questItem(Material.FLINT_AND_STEEL).name("Broken Ceremonial Lighter").build();
	public static final ItemStack lighter = Pugmas20.questItem(Material.FLINT_AND_STEEL).name("Ceremonial Lighter").glow().build();
	public static final ItemStack steel_ingot = Pugmas20.questItem(Material.IRON_INGOT).name("Steel Ingot").glow().build();

	private static final int timerTicks = Time.MINUTE.x(2);
	private static final int torches = 9;
	private static final int treeTorches = 7;
	@Getter
	private static final Location resetLocation = Pugmas20.location(920.5, 86, 313.5, -25.50F, .00F);

	@EventHandler
	public void onFindBrokenLighter(PlayerInteractEntityEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!isAtPugmas(player)) return;

		Entity entity = event.getRightClicked();
		if (!entity.getType().equals(EntityType.ITEM_FRAME)) return;
		if (!Pugmas20.WGUtils.isInRegion(entity.getLocation(), lighterRg)) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (!user.getLightTreeStage().equals(QuestStage.STEP_ONE)) return;

		event.setCancelled(true);

		if (!Quests.hasRoomFor(player, lighter_broken)) {
			user.send(Quests.fullInvError_obtain);
			Quests.sound_villagerNo(player);
			return;
		}

		if (player.getInventory().contains(lighter_broken))
			return;

		ItemUtils.giveItem(player, lighter_broken);
		Quests.sound_obtainItem(player);
		user.send(PREFIX + "You have found the " + lighter_broken.getItemMeta().getDisplayName());

		user.setLightTreeStage(QuestStage.STEP_TWO);
		service.save(user);
	}

	static {
		// Cleanup
		Tasks.async(() -> {
			Pugmas20Service service = new Pugmas20Service();
			List<Pugmas20User> users = service.getAll();
			users.stream().filter(user -> user.isLightingTorches() && user.getLightTreeStage() == QuestStage.STEPS_DONE).forEach(user -> {
				service.cache(user);
				user.resetLightTheTree();
				service.save(user);
			});
		});
	}

	public static void startTimer(Player player) {
		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);

		Countdown timer = Countdown.builder()
				.duration(timerTicks)
				.onStart(() -> {
					user.setLightingTorches(true);
					String format = TimespanFormatter.of(timerTicks / 20).formatType(TimespanFormatType.LONG).format();
					user.send(PREFIX + "You have begun the Pugmas tree lighting ceremony. You have " + format + " to light all the torches!");
				})
				.onSecond(i -> ActionBarUtils.sendActionBar(player, "&e" + TimespanFormatter.of(i).format()))
				.onComplete(() -> {
					user.resetLightTheTree();
					service.save(user);
					user.send(PREFIX + "You ran out of time!");
					user.send(new JsonBuilder(PREFIX + "Click to teleport back to the start").command("/pugmas quests light_the_tree teleportToStart"));
				})
				.start();

		user.setTorchTimerTaskId(timer.getTaskId());
	}

	@EventHandler
	public void onLightTorch(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (!isAtPugmas(player)) return;
		if (block == null) return;

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);
		if (!user.getLightTreeStage().equals(QuestStage.STEPS_DONE)) return;
		if (!lighter.equals(player.getInventory().getItemInMainHand())) return;

		event.setCancelled(true);

		Block placed = block.getType() == Material.NETHERRACK ? block.getRelative(event.getBlockFace()) : block;

		Set<ProtectedRegion> regions = Pugmas20.WGUtils.getRegionsLikeAt("pugmas20_torch_[0-9]+", placed.getLocation());
		if (regions.isEmpty()) return;

		int torch = Integer.parseInt(regions.iterator().next().getId().split("_")[2]);

		if (torch == 1) {
			user.setLightingTorches(true);
			if (user.getTorchTimerTaskId() == -1)
				startTimer(player);
		}

		// TODO PUGMAS Better wording
		if (torch > user.getTorchesLit() + 1) {
			user.send(PREFIX + "You missed a torch!");
			return;
		} else if (torch == user.getTorchesLit() + 1) {
			user.send(PREFIX + "Torch &e#" + torch + " &3of " + torches + " lit");
			SoundUtils.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0.1F);
			user.setTorchesLit(torch);
			service.save(user);
		}

		fire(player, placed.getLocation());

		if (torch == 9) {
			user.setLightTreeStage(QuestStage.FOUND_ALL);
			user.resetLightTheTree();
			service.save(user);

			user.getPlayer().getInventory().removeItem(lighter);

			animateTreeLightBlocks(player);

			int wait = 0;
			for (int i = 1; i <= treeTorches; i++) {
				Location location = getLocation("treetorch", i);
				Tasks.wait(wait += Time.SECOND.get(), () -> {
					fire(player, location);
					SoundUtils.playSound(player, Sound.ENTITY_BLAZE_SHOOT, 0.5F, 0.1F);
				});
			}

			Tasks.wait(wait + 1, () -> {
				SoundUtils.playSound(player, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1F, 0.1F);
				user.setLightTreeStage(QuestStage.COMPLETE);
				user.setMinesStage(QuestStage.NOT_STARTED);
				user.getNextStepNPCs().remove(QuestNPC.ELF1.getId());
				user.getNextStepNPCs().remove(QuestNPC.ELF2.getId());
				service.save(user);

				// TODO PUGMAS Better wording
				user.send(PREFIX + "You lit the tree!");

				Tasks.wait(Time.SECOND, () -> {
					EventUserService eventUserService = new EventUserService();
					EventUser eventUser = eventUserService.get(player);
					eventUser.giveTokens(300);
					eventUserService.save(eventUser);

					eventUser.send(Pugmas20.PREFIX + " You have received 300 Event Tokens!");
				});
			});
		}
	}

	public static Location getLocation(String type, int i) {
		Region region = Pugmas20.WGUtils.getRegion("pugmas20_" + type + "_" + i);
		return Pugmas20.WGUtils.toLocation(region.getMinimumPoint());
	}

	public static void fire(Player player, Location location) {
		Tasks.wait(1, () -> player.sendBlockChange(location, Bukkit.createBlockData(Material.FIRE)));
	}

	public static void air(Player player, Location location) {
		Tasks.wait(1, () -> player.sendBlockChange(location, Bukkit.createBlockData(Material.AIR)));
	}

	static {
		Tasks.repeatAsync(Time.SECOND, Time.SECOND, () -> {
			Pugmas20Service service = new Pugmas20Service();

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!isAtPugmas(player))
					continue;

				Pugmas20User user = service.get(player);

				switch (user.getLightTreeStage()) {
					case STEPS_DONE:
						updateFound(user);
						updateAllTreeTorches(player, LightTheTree::air);
						updateTreeLightBlocksAir(player);
						continue;
					case FOUND_ALL:
						updateFound(user);
						continue;
					case COMPLETE:
						updateAll(player, LightTheTree::fire);
						updateTreeLightBlocks(player);
						continue;
					default:
						updateAll(player, LightTheTree::air);
						updateTreeLightBlocksAir(player);
				}
			}
		});
	}

	private static void updateFound(Pugmas20User user) {
		for (int i = 1; i <= user.getTorchesLit(); i++)
			fire(user.getPlayer(), getLocation("torch", i));
		for (int i = (user.getTorchesLit() + 1); i <= 9; i++)
			air(user.getPlayer(), getLocation("torch", i));
	}

	private static void updateAll(Player player, BiConsumer<Player, Location> method) {
		updateAllTorches(player, method);
		updateAllTreeTorches(player, method);
	}

	private static void updateAllTorches(Player player, BiConsumer<Player, Location> method) {
		for (int i = 1; i <= torches; i++)
			method.accept(player, getLocation("torch", i));
	}

	private static void updateAllTreeTorches(Player player, BiConsumer<Player, Location> method) {
		for (int i = 1; i <= treeTorches; i++)
			method.accept(player, getLocation("treetorch", i));
	}

	private final static Paste treeLightPaster = Pugmas20.WEUtils.paster()
			.at(Pugmas20.location(936, 61, 483))
			.file("pugmas20/tree_light")
			.duration(Time.SECOND.x(7))
			.air(false)
			.computeBlocks();

	public static void updateTreeLightBlocks(Player player) {
		treeLightPaster.buildClientSide(player);
	}

	public static void updateTreeLightBlocksAir(Player player) {
		treeLightPaster.getComputedBlocks().forEach((location, blockData) -> air(player, location));
	}

	public static void animateTreeLightBlocks(Player player) {
		treeLightPaster.buildQueueClientSide(player);
	}

	@EventHandler
	public void onArmorStandInteract(PlayerInteractAtEntityEvent event) {
		if (EquipmentSlot.HAND != event.getHand())
			return;

		Player player = event.getPlayer();
		if (!isAtPugmas(player))
			return;

		Entity entity = event.getRightClicked();
		if (entity.getType() != EntityType.ARMOR_STAND)
			return;

		if (!isAtPugmas(entity.getLocation(), "minerskit"))
			return;

		event.setCancelled(true);

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);

		if (user.getLightTreeStage() != QuestStage.STEP_THREE)
			return;

		if (!Quests.hasRoomFor(player, TheMines.getMinersPickaxe(), TheMines.getMinersSieve())) {
			user.send(Quests.fullInvError_obtain);
			Quests.sound_villagerNo(player);
			return;
		}

		boolean gavePickaxe = false, gaveSieve = false;
		if (!player.getInventory().contains(TheMines.getMinersPickaxe())) {
			ItemUtils.giveItem(player, TheMines.getMinersPickaxe());
			gavePickaxe = true;
		}

		if (!player.getInventory().contains(TheMines.getMinersSieve())) {
			ItemUtils.giveItem(player, TheMines.getMinersSieve());
			gaveSieve = true;
		}

		if (gavePickaxe || gaveSieve) {
			String pickName = TheMines.getMinersPickaxe().getItemMeta().getDisplayName();
			String sieveName = TheMines.getMinersSieve().getItemMeta().getDisplayName();
			String obtained = Pugmas20.PREFIX + " You have obtained a ";

			if (gavePickaxe && gaveSieve)
				user.send(obtained + pickName + " and a " + sieveName);
			else if (gavePickaxe)
				user.send(obtained + pickName);
			else
				user.send(obtained + sieveName);

			Quests.sound_obtainItem(player);
		}
	}

}
