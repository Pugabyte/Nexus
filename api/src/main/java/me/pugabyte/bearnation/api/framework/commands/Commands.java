package me.pugabyte.bearnation.api.framework.commands;

import lombok.Getter;
import me.pugabyte.bearnation.api.framework.annotations.Disabled;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.DoubleSlash;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bearnation.api.utils.Time.Timer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static me.pugabyte.bearnation.api.utils.StringUtils.listLast;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public class Commands {
	private final Plugin plugin;
	private final String path;
	private final CommandMapUtils mapUtils;
	private final Set<Class<? extends CustomCommand>> commandSet;
	@Getter
	private static final Map<String, CustomCommand> commands = new HashMap<>();
	@Getter
	private static final Map<String, Plugin> pluginMap = new HashMap<>();
	@Getter
	private static final Map<Class<?>, Method> converters = new HashMap<>();
	@Getter
	private static final Map<Class<?>, Method> tabCompleters = new HashMap<>();
	@Getter
	private static final Map<String, String> redirects = new HashMap<>();
	@Getter
	private static final String pattern = "\\/(\\/|)[a-zA-Z0-9\\-_]+";

	public Commands(Plugin plugin, String path) {
		this.plugin = plugin;
		this.path = path;
		this.mapUtils = new CommandMapUtils(plugin);
		this.commandSet = new Reflections(path).getSubTypesOf(CustomCommand.class);
		registerConvertersAndTabCompleters();
		plugin.getServer().getPluginManager().registerEvents(new CommandListener(), plugin);
	}

	public static CustomCommand get(String alias) {
		return commands.getOrDefault(alias, null);
	}

	public Commands registerAll() {
		for (Class<? extends CustomCommand> command : commandSet)
			try {
				if (!Modifier.isAbstract(command.getModifiers()) && command.getAnnotation(Disabled.class) == null)
					register(new ObjenesisStd().newInstance(command));
			} catch (Throwable ex) {
				plugin.getLogger().info("Error while registering command " + command.getSimpleName());
				ex.printStackTrace();
			}
		return this;
	}

	private void register(CustomCommand customCommand) {
		new Timer("  Register command " + customCommand.getName(), () -> {
			if (listLast(customCommand.getClass().toString(), ".").startsWith("_")) return;

			try {
				pluginMap.put(customCommand.getClass().getName(), plugin);
				for (String alias : customCommand.getAllAliases()) {
					mapUtils.register(alias, customCommand);

					if (customCommand.getClass().getAnnotation(DoubleSlash.class) != null) alias = "/" + alias;
					commands.put(alias.toLowerCase(), customCommand);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				boolean hasNoArgsConstructor = Stream.of(customCommand.getClass().getConstructors()).anyMatch((c) -> c.getParameterCount() == 0);
				if (customCommand instanceof Listener) {
					if (!hasNoArgsConstructor)
						plugin.getLogger().warning("Cannot register listener on command " + customCommand.getClass().getSimpleName() + ", needs @NoArgsConstructor");
					else
						plugin.getServer().getPluginManager().registerEvents((Listener) customCommand.getClass().newInstance(), plugin);
				} else
					if (new ArrayList<>(getAllMethods(customCommand.getClass(), withAnnotation(EventHandler.class))).size() > 0)
						plugin.getLogger().warning("Found @EventHandlers in " + customCommand.getClass().getSimpleName() + " which does not implement Listener"
								+ (hasNoArgsConstructor ? "" : " or have a @NoArgsConstructor"));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public void unregisterAll() {
		for (Class<? extends CustomCommand> command : commandSet)
			try {
				if (!Modifier.isAbstract(command.getModifiers()))
					unregister(new ObjenesisStd().newInstance(command));
			} catch (Throwable ex) {
				plugin.getLogger().info("Error while unregistering command " + command.getSimpleName());
				ex.printStackTrace();
			}
	}

	private void unregister(CustomCommand customCommand) {
		try {
			pluginMap.remove(customCommand.getClass().getName());
			for (String alias : customCommand.getAllAliases()) {
				mapUtils.unregister(customCommand.getName());
				commands.remove(alias);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			customCommand._shutdown();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void registerConvertersAndTabCompleters() {
		commandSet.forEach(this::registerTabCompleters);
		commandSet.forEach(this::registerConverters);
		registerTabCompleters(CustomCommand.class);
		registerConverters(CustomCommand.class);
	}

	private void registerTabCompleters(Class<?> clazz) {
		getMethods(clazz, withAnnotation(TabCompleterFor.class)).forEach(method -> {
			for (Class<?> classFor : method.getAnnotation(TabCompleterFor.class).value()) {
				method.setAccessible(true);
				tabCompleters.put(classFor, method);
			}
		});
	}

	private void registerConverters(Class<?> clazz) {
		getMethods(clazz, withAnnotation(ConverterFor.class)).forEach(method -> {
			for (Class<?> classFor : method.getAnnotation(ConverterFor.class).value()) {
				method.setAccessible(true);
				converters.put(classFor, method);
			}
		});
	}

	public static Plugin getPlugin(CustomCommand customCommand) {
		return getPlugin(customCommand.getClass().getName());
	}

	public static Plugin getPlugin(String alias) {
		return pluginMap.getOrDefault(alias, null);
	}

}
