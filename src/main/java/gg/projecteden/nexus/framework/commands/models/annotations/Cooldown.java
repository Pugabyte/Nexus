package gg.projecteden.nexus.framework.commands.models.annotations;

import gg.projecteden.utils.TimeUtils.Time;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cooldown {
	Part[] value();
	boolean global() default false;
	String bypass() default "";

	@Target({ElementType.TYPE, ElementType.METHOD})
	@Retention(RetentionPolicy.RUNTIME)
	@Repeatable(value = Cooldown.class)
	@interface Part {
		Time value();
		int x() default 1;

	}
}
