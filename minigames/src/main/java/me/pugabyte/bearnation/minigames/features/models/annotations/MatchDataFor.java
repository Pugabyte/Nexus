package me.pugabyte.bearnation.minigames.features.models.annotations;

import me.pugabyte.bearnation.minigames.features.models.mechanics.Mechanic;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MatchDataFor {
	Class<? extends Mechanic> value();
}
