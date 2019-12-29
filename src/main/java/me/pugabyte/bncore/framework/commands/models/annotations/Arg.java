package me.pugabyte.bncore.framework.commands.models.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {
	String value() default "";
	Class<?> tabCompleter() default void.class;
}
