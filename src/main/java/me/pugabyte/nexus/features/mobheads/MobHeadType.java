package me.pugabyte.nexus.features.mobheads;

import eden.utils.EnumUtils;
import eden.utils.Env;
import lombok.Getter;
import lombok.Setter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.mobheads.common.MobHead;
import me.pugabyte.nexus.features.mobheads.common.MobHeadVariant;
import me.pugabyte.nexus.features.mobheads.variants.AxolotlVariant;
import me.pugabyte.nexus.features.mobheads.variants.CatVariant;
import me.pugabyte.nexus.features.mobheads.variants.CreeperVariant;
import me.pugabyte.nexus.features.mobheads.variants.FoxVariant;
import me.pugabyte.nexus.features.mobheads.variants.HorseVariant;
import me.pugabyte.nexus.features.mobheads.variants.LlamaVariant;
import me.pugabyte.nexus.features.mobheads.variants.MooshroomVariant;
import me.pugabyte.nexus.features.mobheads.variants.PandaVariant;
import me.pugabyte.nexus.features.mobheads.variants.ParrotVariant;
import me.pugabyte.nexus.features.mobheads.variants.RabbitVariant;
import me.pugabyte.nexus.features.mobheads.variants.SheepVariant;
import me.pugabyte.nexus.features.mobheads.variants.SnowmanVariant;
import me.pugabyte.nexus.features.mobheads.variants.TraderLlamaVariant;
import me.pugabyte.nexus.features.mobheads.variants.TropicalFishVariant;
import me.pugabyte.nexus.features.mobheads.variants.VillagerVariant;
import me.pugabyte.nexus.features.mobheads.variants.ZombieVillagerVariant;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@Getter
public enum MobHeadType implements MobHead {
	AXOLOTL(EntityType.AXOLOTL, AxolotlVariant.class, entity -> AxolotlVariant.of((Axolotl) entity)),
	BAT(EntityType.BAT),
	BEE(EntityType.BEE),
	BLAZE(EntityType.BLAZE),
	CAT(EntityType.CAT, CatVariant.class, entity -> CatVariant.of((Cat) entity)),
	CAVE_SPIDER(EntityType.CAVE_SPIDER),
	CHICKEN(EntityType.CHICKEN),
	COD(EntityType.COD),
	COW(EntityType.COW),
	CREEPER(EntityType.CREEPER, CreeperVariant.class, entity -> CreeperVariant.of((Creeper) entity)),
	DOLPHIN(EntityType.DOLPHIN),
	DONKEY(EntityType.DONKEY),
	DROWNED(EntityType.DROWNED),
	ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN),
	ENDER_DRAGON(EntityType.ENDER_DRAGON),
	ENDERMAN(EntityType.ENDERMAN),
	ENDERMITE(EntityType.ENDERMITE),
	EVOKER(EntityType.EVOKER),
	FOX(EntityType.FOX, FoxVariant.class, entity -> FoxVariant.of((Fox) entity)),
	GHAST(EntityType.GHAST),
	GLOW_SQUID(EntityType.GLOW_SQUID),
	GOAT(EntityType.GOAT),
	GUARDIAN(EntityType.GUARDIAN),
	HOGLIN(EntityType.HOGLIN),
	HORSE(EntityType.HORSE, HorseVariant.class, entity -> HorseVariant.of((Horse) entity)),
	HUSK(EntityType.HUSK),
	ILLUSIONER(EntityType.ILLUSIONER),
	IRON_GOLEM(EntityType.IRON_GOLEM),
	LLAMA(EntityType.LLAMA, LlamaVariant.class, entity -> LlamaVariant.of((Llama) entity)),
	MAGMA_CUBE(EntityType.MAGMA_CUBE),
	MUSHROOM_COW(EntityType.MUSHROOM_COW, MooshroomVariant.class, entity -> MooshroomVariant.of((MushroomCow) entity)),
	MULE(EntityType.MULE),
	OCELOT(EntityType.OCELOT),
	PANDA(EntityType.PANDA, PandaVariant.class, entity -> PandaVariant.of((Panda) entity)),
	PARROT(EntityType.PARROT, ParrotVariant.class, entity -> ParrotVariant.of((Parrot) entity)),
	PHANTOM(EntityType.PHANTOM),
	PIG(EntityType.PIG),
	PIGLIN(EntityType.PIGLIN),
	PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE),
	PILLAGER(EntityType.PILLAGER),
	PLAYER(EntityType.PLAYER),
	POLAR_BEAR(EntityType.POLAR_BEAR),
	PUFFERFISH(EntityType.PUFFERFISH),
	RABBIT(EntityType.RABBIT, RabbitVariant.class, entity -> RabbitVariant.of((Rabbit) entity)),
	RAVAGER(EntityType.RAVAGER),
	SALMON(EntityType.SALMON),
	SHEEP(EntityType.SHEEP, SheepVariant.class, entity -> SheepVariant.of((Sheep) entity)),
	SHULKER(EntityType.SHULKER),
	SILVERFISH(EntityType.SILVERFISH),
	SKELETON(EntityType.SKELETON),
	SKELETON_HORSE(EntityType.SKELETON_HORSE),
	SLIME(EntityType.SLIME),
	SNOWMAN(EntityType.SNOWMAN, SnowmanVariant.class, entity -> SnowmanVariant.of((Snowman) entity)),
	SPIDER(EntityType.SPIDER),
	SQUID(EntityType.SQUID),
	STRAY(EntityType.STRAY),
	STRIDER(EntityType.STRIDER),
	TRADER_LLAMA(EntityType.TRADER_LLAMA, TraderLlamaVariant.class, entity -> TraderLlamaVariant.of((TraderLlama) entity)),
	TROPICAL_FISH(EntityType.TROPICAL_FISH, TropicalFishVariant.class, entity -> TropicalFishVariant.RANDOM),
	TURTLE(EntityType.TURTLE),
	VEX(EntityType.VEX),
	VILLAGER(EntityType.VILLAGER, VillagerVariant.class, entity -> VillagerVariant.of((Villager) entity)),
	VINDICATOR(EntityType.VINDICATOR),
	WANDERING_TRADER(EntityType.WANDERING_TRADER),
	WITCH(EntityType.WITCH),
	WITHER(EntityType.WITHER),
	WITHER_SKELETON(EntityType.WITHER_SKELETON),
	WOLF(EntityType.WOLF),
	ZOGLIN(EntityType.ZOGLIN),
	ZOMBIE(EntityType.ZOMBIE),
	ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE),
	ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, ZombieVillagerVariant.class, entity -> ZombieVillagerVariant.of((ZombieVillager) entity)),
	ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN),
	;

	private final EntityType entityType;
	private final Class<? extends MobHeadVariant> variantClass;
	private final Function<Entity, MobHeadVariant> variantConverter;

	@Setter
	private ItemStack genericSkull;
	@Setter
	private double chance;

	public ItemStack getGenericSkull() {
		if (genericSkull == null)
			return null;
		return genericSkull.clone();
	}

	@Getter
	private static final Set<ItemStack> allSkulls = new HashSet<>();

	MobHeadType(EntityType entityType) {
		this(entityType, null, null);
	}

	MobHeadType(EntityType entityType, Class<? extends MobHeadVariant> variantClass, Function<Entity, MobHeadVariant> variantConverter) {
		this.entityType = entityType;
		this.variantClass = variantClass;
		this.variantConverter = variantConverter;
	}

	public static MobHeadType of(EntityType from) {
		return Arrays.stream(values()).filter(entry -> from.equals(entry.getEntityType())).findFirst().orElse(null);
	}

	@Nullable
	public static ItemStack getSkull(Entity entity) {
		MobHeadType mobHeadType = MobHeadType.of(entity.getType());
		if (mobHeadType == null)
			return null;

		return mobHeadType.getSkull(mobHeadType.getVariant(entity));
	}

	public ItemStack getSkull(MobHeadVariant variant) {
		return variant == null ? genericSkull : variant.getSkull();
	}

	public boolean hasVariants() {
		return getVariantClass() != null;
	}

	public List<MobHeadVariant> getVariants() {
		return List.of(getVariantClass().getEnumConstants());
	}

	@Nullable
	public MobHeadVariant getVariant(Entity entity) {
		if (variantConverter == null)
			return null;

		return variantConverter.apply(entity);
	}

	static {
		load();
	}

	static void load() {
		World world = Bukkit.getWorld(Nexus.getEnv() == Env.PROD ? "survival" : "world");
		WorldGuardUtils WGUtils = new WorldGuardUtils(world);
		WorldEditUtils WEUtils = new WorldEditUtils(world);

		for (Block block : WEUtils.getBlocks(WGUtils.getRegion("mobheads"))) {
			try {
				if (!MaterialTag.SIGNS.isTagged(block.getType()))
					continue;

				Sign sign = (Sign) block.getState();
				Directional directional = (Directional) sign.getBlockData();
				ItemStack skull = block.getRelative(directional.getFacing().getOppositeFace()).getRelative(BlockFace.UP)
						.getDrops().stream().findFirst().orElse(null);
				if (skull == null)
					continue;

				EntityType type;
				String entity = (sign.getLine(0) + sign.getLine(1)).trim();
				try {
					type = EntityType.valueOf(entity);
				} catch (Exception ignored) {
					Nexus.log("Cannot parse entity type: " + entity);
					continue;
				}

				double chance = Double.parseDouble(sign.getLine(3));

				skull = new ItemBuilder(skull).name("&e" + camelCase(type) + " Head").build();
				MobHeadType.of(type).setGenericSkull(skull);
				MobHeadType.of(type).setChance(chance);
				allSkulls.add(skull);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		Reflections reflections = new Reflections(MobHeadType.class.getPackage().getName());
		for (Class<? extends MobHeadVariant> variant : reflections.getSubTypesOf(MobHeadVariant.class)) {
			for (Block block : WEUtils.getBlocks(WGUtils.getRegion("mobheads_variant_" + variant.getSimpleName().toLowerCase()))) {
				try {
					if (!MaterialTag.SIGNS.isTagged(block.getType()))
						continue;

					Sign sign = (Sign) block.getState();
					Directional directional = (Directional) sign.getBlockData();
					ItemStack skull = block.getRelative(directional.getFacing().getOppositeFace())
							.getRelative(BlockFace.UP)
							.getDrops().stream()
							.findFirst().orElse(null);

					if (skull == null)
						continue;

					MobHeadVariant mobHeadVariant;
					String variantType = (sign.getLine(0) + sign.getLine(1)).trim();
					try {
						mobHeadVariant = EnumUtils.valueOf(variant, variantType);
					} catch (Exception ignored) {
						Nexus.log("Cannot parse entity variant: " + variant.getSimpleName() + "." + variantType);
						continue;
					}

					skull = new ItemBuilder(skull).name(mobHeadVariant.getDisplayName()).build();
					mobHeadVariant.setItemStack(skull);
					allSkulls.add(skull);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
