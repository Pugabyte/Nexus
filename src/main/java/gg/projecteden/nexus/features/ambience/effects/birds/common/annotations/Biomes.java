package gg.projecteden.nexus.features.ambience.effects.birds.common.annotations;

import gg.projecteden.nexus.utils.BiomeTag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Biomes {
	BiomeTag[] value();

}
