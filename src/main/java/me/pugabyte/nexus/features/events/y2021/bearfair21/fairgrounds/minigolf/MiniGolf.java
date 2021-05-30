package me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf;

import com.destroystokyo.paper.ParticleBuilder;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.BF21PointSource;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners.ProjectileListener;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners.PuttListener;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.listeners.RegionListener;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfColor;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfHole;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfParticle;
import me.pugabyte.nexus.features.particles.ParticleUtils;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21UserService;
import me.pugabyte.nexus.utils.FireworkLauncher;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.SoundUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;
import org.inventivetalent.glow.GlowAPI;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class MiniGolf {
	// @formatter:off
	@Getter private static final ItemStack putter = new ItemBuilder(Material.IRON_HOE).customModelData(901).name("Putter").lore("&7A specialized club", "&7for finishing holes.", "").itemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
	@Getter private static final ItemStack wedge = new ItemBuilder(Material.IRON_HOE).customModelData(903).name("Wedge").lore("&7A specialized club", "&7for tall obstacles", "").itemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
	@Getter private static final ItemStack whistle = new ItemBuilder(Material.IRON_NUGGET).customModelData(901).name("Golf Whistle").lore("&7Returns your last", "&7hit golf ball to its", "&7previous location", "").itemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
	@Getter private static final ItemBuilder golfBall = new ItemBuilder(Material.SNOWBALL).customModelData(901).name("Golf Ball").itemFlags(ItemFlag.HIDE_ATTRIBUTES);
	@Getter private static final ItemStack scoreBook = new ItemBuilder(Material.WRITABLE_BOOK).name("Score Book").itemFlags(ItemFlag.HIDE_ATTRIBUTES).build();
	//
	@Getter private static final List<ItemStack> clubs = Arrays.asList(putter, wedge);
	@Getter private static final List<ItemStack> items = Arrays.asList(putter, wedge, whistle, golfBall.build(), scoreBook);
	//
	@Getter private static final MiniGolf21UserService service = new MiniGolf21UserService();
	@Getter private static final String PREFIX = StringUtils.getPrefix("MiniGolf");
	@Getter private static final double floorOffset = 0.05;
	@Getter private static final double maxVelLen = 2;
	@Getter private static final List<Material> inBounds = Arrays.asList(Material.GREEN_WOOL, Material.GREEN_CONCRETE,
			Material.PETRIFIED_OAK_SLAB, Material.SAND, Material.RED_SAND, Material.SOUL_SOIL, Material.BLUE_ICE,
			Material.PACKED_ICE, Material.ICE, Material.MAGENTA_GLAZED_TERRACOTTA, Material.SLIME_BLOCK, Material.OBSERVER,
			Material.REDSTONE_BLOCK, Material.SPRUCE_FENCE);
	@Getter private static final String gameRegion = BearFair21.getRegion() + "_minigolf";
	@Getter private static final String regionHole = gameRegion + "_hole_";
	// @formatter:on

	public MiniGolf() {
		new ProjectileListener();
		new PuttListener();
		new RegionListener();

		ballTask();
		playerTasks();
		redstoneTask();
	}

	public static void shutdown() {
		for (MiniGolf21User user : service.getUsers()) {
			Snowball snowball = user.getSnowball();
			if (snowball != null) {
				user.removeBall();
				MiniGolfUtils.giveBall(user);
			}
		}
	}

	public static void giveKit(MiniGolf21User user) {
		List<ItemStack> kit = Arrays.asList(getPutter(), getWedge(), getWhistle(),
				getGolfBall().customModelData(user.getMiniGolfColor().getCustomModelData()).build(), getScoreBook());

		PlayerUtils.giveItems(user.getOnlinePlayer(), kit);
	}

	public static void takeKit(MiniGolf21User user) {
		PlayerInventory inventory = user.getOnlinePlayer().getInventory();
		inventory.remove(getPutter());
		inventory.remove(getWedge());
		inventory.remove(getWhistle());
		inventory.remove(getGolfBall().customModelData(user.getMiniGolfColor().getCustomModelData()).build());
		inventory.remove(getScoreBook());
	}

	private void redstoneTask() {
		if (!Nexus.getEnv().equals(Env.PROD))
			return;

		// Hole 13
		String hole13 = MiniGolfHole.THIRTEEN.getRegionId() + "_activate";
		Location hole13Loc = new Location(BearFair21.getWorld(), 101, 119, -28);
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(2), () -> {
			if (BearFair21.getWGUtils().getPlayersInRegion(hole13).size() > 0)
				hole13Loc.getBlock().setType(Material.REDSTONE_BLOCK);
		});

		// Hole 17
		Location hole17Loc = new Location(BearFair21.getWorld(), 107, 117, -9);
		Tasks.repeat(Time.SECOND.x(5), Time.TICK.x(38), () -> {
			if (BearFair21.getWGUtils().getPlayersInRegion(gameRegion + "_play_top").size() > 0)
				hole17Loc.getBlock().setType(Material.REDSTONE_BLOCK);
		});
	}

	private void playerTasks() {
		// Kit
		Tasks.repeat(Time.SECOND.x(5), Time.SECOND.x(2), () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!BearFair21.isAtBearFair(player))
					continue;

				int regions = BearFair21.getWGUtils().getRegionsLikeAt(gameRegion + "_play_.*", player.getLocation()).size();

				MiniGolf21User user = service.get(player);
				if (user.isPlaying() && regions == 0 && player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerUtils.runCommand(player, "minigolf quit");
				} else if (!user.isPlaying() && regions > 0 && player.getGameMode().equals(GameMode.SURVIVAL)) {
					PlayerUtils.runCommand(player, "minigolf play");
				}

			}
		});

		// Power
		Tasks.repeat(Time.SECOND.x(5), Time.TICK, () -> {
			for (MiniGolf21User user : new HashSet<>(service.getUsers())) {
				if (!user.isOnline())
					continue;

				if (user.getSnowball() == null)
					continue;

				Player player = user.getOnlinePlayer();
				ItemStack tool = ItemUtils.getTool(player);
				if (ItemUtils.isNullOrAir(tool))
					continue;

				// quick fix
				ItemStack clone = tool.clone();
				clone.setAmount(1);
				boolean stop = true;
				for (ItemStack _item : MiniGolf.getClubs()) {
					if (ItemUtils.isFuzzyMatch(clone, _item))
						stop = false;
				}
				if (stop)
					continue;
				//

				if (player.getLevel() != 0)
					player.setLevel(0);

				double amount = player.spigot().getPing() < 200 ? 0.04 : 0.02;
				double exp = player.getExp() + amount;
				if (exp > 1.00) {
					exp = 0.00;
				}

				player.setExp((float) exp);
			}
		});
	}

	private void ballTask() {
		AtomicInteger i = new AtomicInteger(1);
		AtomicReference<MiniGolfColor> color = new AtomicReference<>(MiniGolfColor.RED);

		Tasks.repeat(Time.SECOND.x(5), Time.TICK, () -> {
			boolean updateRainbow = false;
			i.getAndIncrement();
			if (i.getAndIncrement() % 20 == 0) {

				int ndx = color.get().ordinal();
				if (ndx < (MiniGolfColor.values().length - 2))
					ndx += 1;
				else
					ndx = 2;

				color.set(MiniGolfColor.values()[ndx]);
				updateRainbow = true;
			}

			for (MiniGolf21User user : new HashSet<>(service.getUsers())) {
				Snowball ball = user.getSnowball();
				if (ball == null)
					continue;

				if (!ball.isValid()) {
					user.removeBall();
					continue;
				}

				// Check block underneath
				Location loc = ball.getLocation();
				Block below = loc.subtract(0, 0.1, 0).getBlock();

				Vector vel = ball.getVelocity();

				// Particles
				MiniGolfParticle miniGolfParticle = user.getMiniGolfParticle();
				if (miniGolfParticle != null && vel.length() > 0.01) {
					try {
						Particle particle = miniGolfParticle.getParticle();
						ParticleBuilder particleBuilder = new ParticleBuilder(particle)
								.location(ball.getLocation().add(0, floorOffset, 0))
								.count(1)
								.extra(0);

						if (particle.equals(Particle.REDSTONE)) {
							if (user.getMiniGolfColor().equals(MiniGolfColor.RAINBOW)) {
								int[] rgb = ParticleUtils.incRainbow(ball.getTicksLived());
								DustOptions dustOptions = ParticleUtils.newDustOption(particle, rgb[0], rgb[1], rgb[2]);
								particleBuilder.data(dustOptions);
							} else
								particleBuilder.color(user.getColor());
						}
						particleBuilder.spawn();
					} catch (Exception ignored) {
					}
				}

				// Rainbow Glow
				if (user.getMiniGolfColor().equals(MiniGolfColor.RAINBOW)) {
					if (updateRainbow)
						GlowAPI.setGlowing(ball, color.get().getColorType().getGlowColor(), user.getOnlinePlayer());
				}

				Material _type = loc.getBlock().getType();
				if (_type.equals(Material.LAVA) || _type.equals(Material.WATER)) {
					MiniGolfUtils.respawnBall(ball);
					continue;
				}

				// Act upon block type
				Material type = below.getType();
				MiniGolfHole ballHole = MiniGolfUtils.getHole(ball.getLocation());
				switch (type) {
					case CAULDRON:
						// Check speed
						if (vel.getY() >= 0 && vel.length() > 0.34)
							continue;

						if (ballHole == null || !ballHole.equals(user.getCurrentHole())) {
							MiniGolfUtils.respawnBall(ball);
							continue;
						}

						// Halt velocity
						ball.setVelocity(new Vector(0, ball.getVelocity().getY(), 0));

						// Remove ball
						user.removeBall();

						// Spawn firework
						Tasks.wait(Time.TICK, () -> new FireworkLauncher(loc)
								.power(0)
								.detonateAfter(Time.TICK.x(2))
								.type(Type.BURST)
								.colors(user.getFireworkColor())
								.fadeColors(Collections.singletonList(Color.WHITE))
								.launch());

						// Send message
						int wait = Time.SECOND.x(2);
						if (BearFair21.checkDailyTokens(user.getPlayer(), BF21PointSource.MINIGOLF, 5) > 0)
							wait = 0;

						BearFair21.giveDailyPoints(user.getPlayer(), BF21PointSource.MINIGOLF, 5);
						int strokes = user.getCurrentStrokes();
						String userScore = MiniGolfUtils.getScore(user);
						Tasks.wait(wait, () ->
								MiniGolfUtils.sendActionBar(user, "&6Stroke: " + strokes + " (" + userScore + ")"));

						MiniGolfUtils.giveBall(user);

						if (strokes == 1 && !user.isRainbow()) {
							user.getHoleInOne().add(ballHole);
							MiniGolfUtils.checkHoleInOnes(user);
						}

						if (user.getScore().containsKey(ballHole)) {
							int score = user.getScore().get(ballHole);
							if (strokes < score) {
								user.getScore().put(ballHole, strokes);
							}
						} else {
							user.getScore().put(ballHole, strokes);
						}

						user.setCurrentHole(null);
						user.setCurrentStrokes(0);
						service.save(user);
						break;
					case AIR:
					case WATER:
					case LAVA:
					case CRIMSON_HYPHAE:
					case PURPLE_STAINED_GLASS:
					case BARRIER:
						// Fall
						ball.setGravity(true);
						break;
					case ICE:
					case PACKED_ICE:
					case BLUE_ICE:
						// No friction, constant speed
						ball.setVelocity(vel);
						break;
					case SLIME_BLOCK:
						// Bounce, with no friction
						vel.setY(0.30);
						ball.setVelocity(vel);
						break;
					case REDSTONE_BLOCK:
						// Boost
						if (vel.length() < maxVelLen)
							ball.setVelocity(vel.multiply(1.3));
						break;
					case SOUL_SOIL:
						// Stop bouncing
						vel.setY(0);
						ball.setVelocity(vel);
					case SAND:
					case RED_SAND:
						// Friction
						vel.multiply(0.9);
						ball.setVelocity(vel);
						break;
					case MAGENTA_GLAZED_TERRACOTTA:
						// Get Direction
						Directional directional = (Directional) below.getBlockData();

						Vector dir = getDirection(directional.getFacing(), 0.1);
						if (dir == null)
							continue;

						// Push ball
						ball.setVelocity(vel.multiply(9.0).add(dir).multiply(0.1)); // 9.0

						break;
					case OBSERVER:
						// Get Direction
						directional = (Directional) below.getBlockData();
						dir = getDirection(directional.getFacing(), 0.5);
						if (dir == null)
							continue;

						if (vel.length() < maxVelLen) {
							// Push ball
							ball.setVelocity(vel.multiply(9.3).add(dir).multiply(0.1));
						}

						break;
					case DISPENSER:
						Block under = below.getRelative(BlockFace.DOWN);
						if (MaterialTag.SIGNS.isTagged(under.getType())) {
							Sign sign = (Sign) under.getState();
							String line4 = sign.getLine(3);
							String[] split = line4.split(",");
							if (split.length == 3) {
								try {
									int x = Integer.parseInt(split[0]);
									int y = Integer.parseInt(split[1]);
									int z = Integer.parseInt(split[2]);
									Location newLoc = new Location(ball.getWorld(), x, y, z);
									ball.setVelocity(new Vector(0, 0, 0));
									ball.teleport(LocationUtils.getCenteredLocation(newLoc));
									ball.setGravity(true);
								} catch (Exception ignored) {
								}
							}
						}

						break;
					case SMOKER:
						// Get Direction
						under = below;
						directional = (Directional) under.getBlockData();
						BlockFace facing = directional.getFacing();
						under = under.getRelative(facing.getOppositeFace());
						if (MaterialTag.SIGNS.isTagged(under.getType())) {
							Sign sign = (Sign) under.getState();
							String heightStr = sign.getLine(2).replaceAll("height", "");
							String powerStr = sign.getLine(3).replaceAll("power", "");
							try {
								Location newLoc = LocationUtils.getCenteredLocation(below.getRelative(facing).getLocation());
								ball.setVelocity(new Vector(0, 0, 0));
								ball.teleport(LocationUtils.getCenteredLocation(newLoc));
								ball.setGravity(true);

								double height = Double.parseDouble(heightStr);
								double power = Double.parseDouble(powerStr);
								Vector newVel = getDirection(facing.getOppositeFace(), power);

								ball.setVelocity(ball.getVelocity().multiply(9.3).add(newVel).setY(height));
								SoundUtils.playSound(ball.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 3, 1);
								new ParticleBuilder(Particle.EXPLOSION_NORMAL).location(ball.getLocation()).count(25).spawn();
							} catch (Exception ignored) {

							}
						}
						break;
					default:
						// Check if floating above slabs
						if (MiniGolfUtils.isBottomSlab(below) && loc.getY() > below.getY() + 0.5)
							ball.setGravity(true);

						if (ball.getLocation().getY() < 0) {
							MiniGolfUtils.respawnBall(ball);
							break;
						}

						// Stop & respawn ball if slow enough
						if (vel.getY() >= 0 && vel.length() <= 0.01) {
							ball.setVelocity(new Vector(0, 0, 0));
							ball.setGravity(false);
							ball.teleport(ball.getLocation());

							if (!MiniGolfUtils.isInBounds(user, ball.getLocation()))
								MiniGolfUtils.respawnBall(ball);

							break;
						}

						// Slight friction
						vel.multiply(0.975);
						ball.setVelocity(vel);
						break;
				}
			}
		});
	}

	private Vector getDirection(BlockFace face, double power) {
		Vector vector = switch (face) {
			case NORTH -> new Vector(0, 0, power);
			case SOUTH -> new Vector(0, 0, -power);
			case EAST -> new Vector(-power, 0, 0);
			case WEST -> new Vector(power, 0, 0);
			default -> null;
		};

		return vector;
	}

}
