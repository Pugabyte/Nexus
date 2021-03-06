package gg.projecteden.nexus.features.customenchants;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.SneakyThrows;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;

import static gg.projecteden.nexus.utils.Utils.registerListeners;

public class CustomEnchantsRegistration {

	public static void register() {
		try {
			setAcceptingNew();

			for (Class<? extends CustomEnchant> clazz : getClasses())
				register(clazz);

			registerListeners(getEnchantsPackage());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void unregister() {
		for (CustomEnchant enchant : CustomEnchants.getEnchants()) {
			try {
				getByKey().remove(enchant.getKey());
				getByName().remove(enchant.getName());
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@SneakyThrows
	static void register(Class<? extends CustomEnchant> clazz) {
		CustomEnchant enchant = clazz.getConstructor(NamespacedKey.class).newInstance(CustomEnchants.getKey(clazz));
		Enchantment.registerEnchantment(enchant);
		CustomEnchants.getEnchantsMap().put(clazz, enchant);
	}

	@NotNull
	static NamespacedKey getKey(String id) {
		final NamespacedKey key = NamespacedKey.fromString(id, Nexus.getInstance());
		if (key == null)
			throw new InvalidInputException("[CustomEnchants] Could not generate NamespacedKey for " + id);
		return key;
	}

	private static void setAcceptingNew() throws NoSuchFieldException, IllegalAccessException {
		Field fieldAcceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
		fieldAcceptingNew.setAccessible(true);
		fieldAcceptingNew.set(null, true);
	}

	private static Set<Class<? extends CustomEnchant>> getClasses() {
		final Reflections reflections = new Reflections(getEnchantsPackage());
		return reflections.getSubTypesOf(CustomEnchant.class);
	}

	@NotNull
	private static String getEnchantsPackage() {
		return CustomEnchants.class.getPackage().getName() + ".enchants";
	}

	@SneakyThrows
	private static Map<NamespacedKey, Enchantment> getByKey() {
		Field fieldByKey = Enchantment.class.getDeclaredField("byKey");
		fieldByKey.setAccessible(true);
		return (Map<NamespacedKey, Enchantment>) fieldByKey.get(null);
	}

	@SneakyThrows
	private static Map<String, Enchantment> getByName() {
		Field fieldByName = Enchantment.class.getDeclaredField("byName");
		fieldByName.setAccessible(true);
		return (Map<String, Enchantment>) fieldByName.get(null);
	}
}
