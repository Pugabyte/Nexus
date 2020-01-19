package me.pugabyte.bncore.framework.commands.models;

import com.google.common.base.Strings;
import lombok.SneakyThrows;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.Commands;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.commands.models.events.TabEvent;
import me.pugabyte.bncore.framework.exceptions.BNException;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.NoPermissionException;
import me.pugabyte.bncore.framework.exceptions.preconfigured.PlayerNotFoundException;
import org.bukkit.command.CommandSender;
import org.objenesis.ObjenesisStd;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static me.pugabyte.bncore.utils.Utils.listLast;
import static org.reflections.ReflectionUtils.getMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

@SuppressWarnings("unused")
public interface ICustomCommand {

	default void execute(CommandEvent event) {
		try {
			CustomCommand command = getCommand(event);
			Method method = getMethod(event);
			if (!hasPermission(event.getSender(), method))
				throw new NoPermissionException();
			command.invoke(method, event);
		} catch (Exception ex) {
			event.handleException(ex);
		}
	}

	default List<String> tabComplete(TabEvent event) {
		try {
			getCommand(event);
			return new PathParser(event).tabComplete(event);
		} catch (Exception ex) {
			event.handleException(ex);
		}
		return new ArrayList<>();
	}

	default String getName() {
		return getAliases().get(0);
	}

	default List<String> getAliases() {
		String name = listLast(this.getClass().toString(), ".").replaceAll("Command", "");
		List<String> aliases = new ArrayList<>(Collections.singletonList(name.toLowerCase()));

		for (Annotation annotation : this.getClass().getAnnotations()) {
			if (annotation instanceof Aliases) {
				for (String alias : ((Aliases) annotation).value()) {
					if (!Pattern.compile("[a-zA-Z0-9_-]+").matcher(alias).matches()) {
						BNCore.warn("Alias invalid: " + name + "Command.java / " + alias);
						continue;
					}

					aliases.add(alias);
				}
			}
		}

		return aliases;
	}

	default String getPermission() {
		for (Annotation annotation : this.getClass().getAnnotations()) {
			if (annotation instanceof Permission) {
				return ((Permission) annotation).value();
			}
		}

		return null;
	}

	default void invoke(Method method, CommandEvent event) throws Exception {
		List<String> args = event.getArgs();
		List<Parameter> parameters = Arrays.asList(method.getParameters());
		Iterator<String> path = Arrays.asList(method.getAnnotation(Path.class).value().split(" ")).iterator();
		Object[] objects = new Object[parameters.size()];

		int i = 1;
		int pathIndex = 0;
		for (Parameter parameter : parameters) {
			Arg annotation = parameter.getDeclaredAnnotation(Arg.class);
			if (annotation == null)
				throw new BNException("Command parameter not annotated with @Arg: "
						+ method.getName() + "(" + parameter.getType().getName() + " " + parameter.getName() + ")");

			String pathArg = "";
			while (!pathArg.startsWith("{") && !pathArg.startsWith("[") && !pathArg.startsWith("<")) {
				pathArg = path.next();
				++pathIndex;
			}

			String value = annotation.value();
			if (args.size() >= pathIndex) {
				if (pathArg.contains("..."))
					value = String.join(" ", args.subList(pathIndex - 1, args.size()));
				else
					value = args.get(pathIndex - 1);
			}

			boolean required = pathArg.startsWith("<");

			objects[i - 1] = convert(value, parameter.getType(), event.getCommand(), required);
			++i;
		}

		method.setAccessible(true);
		method.invoke(this, objects);
	}

	List<Class<? extends Exception>> conversionExceptions = Arrays.asList(
			InvalidInputException.class,
			PlayerNotFoundException.class
	);

	@SneakyThrows
	default Object convert(String value, Class<?> type, CustomCommand command, boolean required) {
		try {
			if (Commands.getConverters().containsKey(type)) {
				Method converter = Commands.getConverters().get(type);
				boolean isAbstract = Modifier.isAbstract(converter.getDeclaringClass().getModifiers());
				if (isAbstract || converter.getDeclaringClass().equals(command.getClass()))
					return Commands.getConverters().get(type).invoke(command, value);
				else {
					CustomCommand newCommand = getNewCommand(command.getEvent(), converter.getDeclaringClass());
					return Commands.getConverters().get(type).invoke(newCommand, value);
				}
			}
		} catch (InvocationTargetException ex) {
			if (!required)
				if (conversionExceptions.contains(ex.getCause().getClass()))
					return null;

			throw ex;
		}

		// TODO: Better error messages
		if (Strings.isNullOrEmpty(value))
			if (required)
				throw new InvalidInputException("Missing arguments");
			else
				return null;

		if (Boolean.class == type || Boolean.TYPE == type) {
			if (Arrays.asList("enable", "on", "yes", "1").contains(value)) value = "true";
			return Boolean.parseBoolean(value);
		}
		if (Integer.class == type || Integer.TYPE == type) return Integer.parseInt(value);
		if (Double.class == type || Double.TYPE == type) return Double.parseDouble(value);
		if (Float.class == type || Float.TYPE == type) return Float.parseFloat(value);
		if (Short.class == type || Short.TYPE == type) return Short.parseShort(value);
		if (Long.class == type || Long.TYPE == type) return Long.parseLong(value);
		if (Byte.class == type || Byte.TYPE == type) return Byte.parseByte(value);
		return value;
	}

	@SneakyThrows
	default CustomCommand getCommand(CommandEvent event) {
		Constructor<? extends CustomCommand> constructor = event.getCommand().getClass().getDeclaredConstructor(CommandEvent.class);
		constructor.setAccessible(true);
		CustomCommand command = constructor.newInstance(event);
		event.setCommand(command);
		return command;
	}

	@SneakyThrows
	default CustomCommand getNewCommand(CommandEvent originalEvent, Class<?> clazz) {
		CustomCommand customCommand = new ObjenesisStd().newInstance((Class<? extends CustomCommand>) clazz);
		CommandEvent newEvent = new CommandEvent(originalEvent.getSender(), customCommand, new ArrayList<>());
		return getCommand(newEvent);
	}

	default Set<Method> getPathMethods() {
		return getMethods(this.getClass(), withAnnotation(Path.class));
	}

	// TODO: Use same methods as tab complete
	default Method getMethod(CommandEvent event) {
		Method method = new PathParser(event).match(event.getArgs());

		if (method == null)
			// TODO No default path, what do?
			throw new InvalidInputException("No matching path");

		return method;
	}

	default boolean hasPermission(CommandSender sender, Method method) {
		String permission = getPermission();
		if (permission != null && !sender.hasPermission(permission))
			return false;

		if (method.isAnnotationPresent(Permission.class)) {
			Permission pathPermission = method.getAnnotation(Permission.class);
			permission = pathPermission.absolute() ? "" : (permission + ".") + pathPermission.value();
			if (!sender.hasPermission(permission))
				return false;
		}

		return true;
	}

}


