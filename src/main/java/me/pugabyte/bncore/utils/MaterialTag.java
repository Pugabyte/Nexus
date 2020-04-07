package me.pugabyte.bncore.utils;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

public class MaterialTag implements Tag<Material> {
	public static final MaterialTag WOOL = new MaterialTag("_WOOL", MatchMode.SUFFIX);
	public static final MaterialTag DYES = new MaterialTag("_DYE", MatchMode.SUFFIX);
	public static final MaterialTag CARPET = new MaterialTag("_CARPET", MatchMode.SUFFIX);
	public static final MaterialTag BEDS = new MaterialTag("_BED", MatchMode.SUFFIX);
	public static final MaterialTag ALL_BANNERS = new MaterialTag(Tag.BANNERS);
	public static final MaterialTag BANNERS = new MaterialTag("_BANNER", MatchMode.SUFFIX);
	public static final MaterialTag WALL_BANNERS = new MaterialTag("_WALL_BANNER", MatchMode.SUFFIX);
	public static final MaterialTag STAINED_GLASS = new MaterialTag("_STAINED_GLASS", MatchMode.SUFFIX);
	public static final MaterialTag STAINED_GLASS_PANES = new MaterialTag("_STAINED_GLASS_PANE", MatchMode.SUFFIX);
	public static final MaterialTag TERRACOTTAS = new MaterialTag("_TERRACOTTA", MatchMode.SUFFIX);
	public static final MaterialTag GLAZED_TERRACOTTAS = new MaterialTag("_GLAZED_TERRACOTTA", MatchMode.SUFFIX);
	public static final MaterialTag ALL_CONCRETES = new MaterialTag("CONCRETE", MatchMode.CONTAINS);
	public static final MaterialTag CONCRETES = new MaterialTag("_CONCRETE", MatchMode.SUFFIX);
	public static final MaterialTag CONCRETE_POWDERS = new MaterialTag("_CONCRETE_POWDER", MatchMode.SUFFIX);
	public static final MaterialTag SHULKER_BOXES = new MaterialTag("_SHULKER_BOX", MatchMode.SUFFIX);

	public static final MaterialTag COLORABLE = new MaterialTag(WOOL, DYES, CARPET, BEDS, BANNERS, WALL_BANNERS, STAINED_GLASS,
			STAINED_GLASS_PANES, TERRACOTTAS, GLAZED_TERRACOTTAS, CONCRETES, CONCRETE_POWDERS, SHULKER_BOXES);

	public static final MaterialTag CORAL_WALL_FANS = new MaterialTag("_WALL_FAN", MatchMode.SUFFIX);

	public static final MaterialTag PLANTS = new MaterialTag(Material.GRASS, Material.FERN, Material.DEAD_BUSH,
			Material.DANDELION, Material.POPPY, Material.BLUE_ORCHID, Material.ALLIUM, Material.AZURE_BLUET,
			Material.RED_TULIP, Material.ORANGE_TULIP, Material.WHITE_TULIP, Material.PINK_TULIP, Material.OXEYE_DAISY,
			Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.LILY_PAD, Material.KELP, Material.KELP_PLANT)
			.append(CORAL_WALL_FANS)
			.append(Tag.CORALS);

	public static final MaterialTag SKULLS = new MaterialTag("_SKULL", MatchMode.SUFFIX).append("_HEAD", MatchMode.SUFFIX);
	public static final MaterialTag BOATS = new MaterialTag(Tag.ITEMS_BOATS);
	public static final MaterialTag SPAWN_EGGS = new MaterialTag("_SPAWN_EGG", MatchMode.SUFFIX);
	public static final MaterialTag PORTALS = new MaterialTag(Material.END_PORTAL, Material.NETHER_PORTAL);
	public static final MaterialTag LIQUIDS = new MaterialTag(Material.WATER, Material.LAVA);
	public static final MaterialTag CONTAINERS = new MaterialTag(Material.FURNACE, Material.DISPENSER, Material.CHEST,
			Material.ENDER_CHEST, Material.ANVIL, Material.BREWING_STAND, Material.TRAPPED_CHEST, Material.HOPPER, Material.DROPPER)
			.append("_SHULKER_BOX", MatchMode.SUFFIX);
	public static final MaterialTag PRESSURE_PLATES = new MaterialTag("_PRESSURE_PLATE", MatchMode.SUFFIX);

	private final EnumSet<Material> materials;
	private final NamespacedKey key = null;

	static {
		for (DyeColor value : DyeColor.values())
			COLORABLE.append(value + "_", MatchMode.PREFIX);

	}

	public MaterialTag(EnumSet<Material> materials) {
		this.materials = materials.clone();
	}

	@SafeVarargs
	public MaterialTag(Tag<Material>... materialTags) {
		this.materials = EnumSet.noneOf(Material.class);
		append(materialTags);
	}

	public MaterialTag(Material... materials) {
		this.materials = EnumSet.noneOf(Material.class);
		append(materials);
	}

	public MaterialTag(String segment, MatchMode mode) {
		this.materials = EnumSet.noneOf(Material.class);
		append(segment, mode);
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	public MaterialTag append(Material... materials) {
		this.materials.addAll(Arrays.asList(materials));
		return this;
	}

	@SafeVarargs
	public final MaterialTag append(Tag<Material>... materialTags) {
		for (Tag<Material> materialTag : materialTags) {
			this.materials.addAll(materialTag.getValues());
		}

		return this;
	}

	public MaterialTag append(String segment, MatchMode mode) {
		segment = segment.toUpperCase();

		switch (mode) {
			case PREFIX:
				for (Material m : Material.values())
					if (m.name().startsWith(segment))
						materials.add(m);
				break;

			case SUFFIX:
				for (Material m : Material.values())
					if (m.name().endsWith(segment))
						materials.add(m);
				break;

			case CONTAINS:
				for (Material m : Material.values())
					if (m.name().contains(segment))
						materials.add(m);
				break;
		}

		return this;
	}

	public MaterialTag exclude(Material... materials) {
		for (Material m : materials) {
			this.materials.remove(m);
		}

		return this;
	}

	@SafeVarargs
	public final MaterialTag exclude(Tag<Material>... materialTags) {
		for (Tag<Material> materialTag : materialTags) {
			this.materials.removeAll(materialTag.getValues());
		}

		return this;
	}

	public MaterialTag exclude(String segment, MatchMode mode) {

		segment = segment.toUpperCase();

		switch (mode) {
			case PREFIX:
				for (Material m : Material.values())
					if (m.name().startsWith(segment))
						materials.remove(m);
				break;

			case SUFFIX:
				for (Material m : Material.values())
					if (m.name().endsWith(segment))
						materials.remove(m);
				break;

			case CONTAINS:
				for (Material m : Material.values())
					if (m.name().contains(segment))
						materials.remove(m);
				break;
		}

		return this;
	}

	@Override
	public Set<Material> getValues() {
		return materials;
	}

	@Override
	public boolean isTagged(Material material) {
		return materials.contains(material);
	}

	@Override
	public String toString() {
		return materials.toString();
	}

	public enum MatchMode {
		PREFIX,
		SUFFIX,
		CONTAINS
	}

}
