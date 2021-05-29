package me.pugabyte.nexus.features.events.y2021.bearfair21.quests.resources;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import eden.utils.TimeUtils.Time;
import eden.utils.Utils.MinMaxResult;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.models.task.Task;
import me.pugabyte.nexus.models.task.TaskService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils.Jingle;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldEditUtils.Paste;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static eden.utils.StringUtils.camelCase;
import static eden.utils.Utils.getMin;
import static me.pugabyte.nexus.utils.BlockUtils.createDistanceSortedQueue;
import static me.pugabyte.nexus.utils.RandomUtils.randomInt;

public class WoodCutting implements Listener {
	private static final String taskId = "bearfair21-tree-regen";
	private static final String tree_region = BearFair21.getRegion() + "_trees";
	@Getter
	private static boolean treeAnimating = false;

	public WoodCutting() {
		Nexus.registerListener(this);

		Tasks.repeatAsync(Time.SECOND, Time.SECOND.x(1), () -> {
			TaskService service = new TaskService();
			service.process(taskId).forEach(task -> {
				Map<String, Object> data = task.getJson();

				BearFair21TreeType treeType = BearFair21TreeType.valueOf((String) data.get("tree"));
				int id = Double.valueOf((double) data.get("id")).intValue();

				treeType.build(id);

				service.complete(task);
			});
		});
	}

	public static boolean breakBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (!BearFair21.isInRegionRegex(event.getBlock().getLocation(), tree_region + ".*")) {
			player.sendMessage("not in tree region");
			return false;
		}

		BearFair21TreeType treeType = BearFair21TreeType.of(event.getBlock().getType());
		if (treeType == null) {
			player.sendMessage("tree type is null");
			return false;
		}

		Set<ProtectedRegion> regions = BearFair21.getWGUtils().getRegionsLike(tree_region + "_" + treeType.name() + "_[0-9]+");

		MinMaxResult<ProtectedRegion> result = getMin(regions, region ->
				event.getBlock().getLocation().distance(BearFair21.getWGUtils().toLocation(region.getMinimumPoint())));

		ProtectedRegion region = result.getObject();
		double distance = result.getValue().doubleValue();

		if (region == null) {
			player.sendMessage("region is null");
			return false;
		}

		int tree = Integer.parseInt(region.getId().split("_")[3]);

		if (tree < 1 || distance > 5) {
			player.sendMessage("tree < 1 || distance > 5");
			return false;
		}

		player.sendMessage("felling tree...");
		treeType.feller(event.getPlayer(), tree);
		return true;
	}

	public enum BearFair21TreeType {
		OAK(Material.OAK_WOOD, Material.OAK_LEAVES),
		;

		@Getter
		private final Material logs;
		@Getter
		private final List<Material> others;

		@Getter
		private final Map<Integer, Paste> pasters = new HashMap<>();
		@Getter
		private final Map<Integer, Queue<Location>> queues = new HashMap<>();
		@Getter
		private final Map<Integer, ProtectedRegion> regions = new HashMap<>();

		private static final int animationTime = Time.SECOND.x(3);

		BearFair21TreeType(Material logs, Material... others) {
			this.logs = logs;
			this.others = Arrays.asList(others);

			Tasks.async(() -> {
				for (int id = 1; id <= 10; id++) {
					if (getRegion(id) == null)
						continue;

					getPaster(id);
					getQueue(id);
				}
			});
		}

		public ItemStack getLog() {
			return getLog(1);
		}

		public ItemStack getLog(int amount) {
			return new ItemBuilder(logs).name(camelCase(name() + " Logs")).amount(amount).build();
		}

		public List<Material> getAllMaterials() {
			ArrayList<Material> materials = new ArrayList<>(others);
			materials.add(logs);
			return materials;
		}

		public String getAllMaterialsString() {
			return getAllMaterials().stream().map(material -> material.name().toLowerCase()).collect(Collectors.joining(","));
		}

		public static BearFair21TreeType of(Material logs) {
			for (BearFair21TreeType treeType : BearFair21TreeType.values())
				if (treeType.getLogs() == logs)
					return treeType;

			return null;
		}

		public void build(int id) {
			treeAnimating = true;
			getPaster(id).buildQueue().thenAccept($ -> treeAnimating = false);
		}

		private Queue<Location> getQueue(int id) {
			queues.computeIfAbsent(id, $ -> {
				ProtectedRegion region = getRegion(id);
				if (region == null)
					return null;

				Location base = BearFair21.getWEUtils().toLocation(region.getMinimumPoint());
				Queue<Location> queue = createDistanceSortedQueue(base);
				queue.addAll(getBlocks(id).keySet());
				return queue;
			});

			return queues.get(id);
		}

		private Map<Location, BlockData> getBlocks(int id) {
			return getPaster(id).getComputedBlocks();
		}

		private Paste getPaster(int id) {
			pasters.computeIfAbsent(id, $ -> {
				ProtectedRegion region = getRegion(id);
				if (region == null)
					return null;

				String schematicName = region.getId().replaceAll("_", "/");
				return BearFair21.getWEUtils().paster()
						.air(false)
						.at(region.getMinimumPoint())
						.duration(animationTime)
						.file(schematicName)
						.computeBlocks();
			});

			return pasters.get(id);
		}

		public ProtectedRegion getRegion(int id) {
			regions.computeIfAbsent(id, $ -> {
				try {
					return BearFair21.getWGUtils().getProtectedRegion(tree_region + "_" + name().toLowerCase() + "_" + id);
				} catch (InvalidInputException ex) {
					return null;
				}
			});

			return regions.get(id);
		}

		public void feller(Player player, int id) {
			if (!new CooldownService().check(StringUtils.getUUID0(), getRegion(id).getId(), Time.SECOND.x(3)))
				return;

			treeAnimating = true;
			Tasks.async(() -> {
				Queue<Location> queue = new PriorityQueue<>(getQueue(id));

				int wait = 0;
				int blocksPerTick = Math.max(queue.size() / animationTime, 1);

				queueLoop:
				while (true) {
					++wait;
					for (int i = 0; i < blocksPerTick; i++) {
						Location poll = queue.poll();
						if (poll == null)
							break queueLoop;

						Tasks.wait(wait, () -> poll.getBlock().setType(Material.AIR, true));
					}
				}

				Tasks.wait(++wait, () -> treeAnimating = false);

				Tasks.Countdown.builder()
						.duration(randomInt(8, 12) * 4)
						.onTick(i -> {
							if (i % 4 == 0)
								PlayerUtils.giveItem(player, getLog());
						})
						.start();

				Jingle.TREE_FELLER.play(player);

				onBreak(id);
			});
		}

		public void onBreak(int id) {
			new TaskService().save(new Task(taskId, new HashMap<>() {{
				put("tree", name());
				put("id", id);
			}}, LocalDateTime.now().plusSeconds(randomInt(3 * 60, 5 * 60))));
		}
	}


}
