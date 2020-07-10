package me.pugabyte.bearnation.minigames.features.menus.annotations;

import me.pugabyte.bearnation.minigames.features.models.mechanics.Mechanic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CustomMechanicSettings {
	Class<? extends Mechanic>[] value();
}
