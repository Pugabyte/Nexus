package me.pugabyte.bearnation.minigames.features.models.annotations;

import me.pugabyte.bearnation.minigames.features.models.scoreboards.MinigameScoreboard.Type;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Scoreboard {
	Type sidebarType() default Type.MATCH;
	boolean teams() default true;
}
