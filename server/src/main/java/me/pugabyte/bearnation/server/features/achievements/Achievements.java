package me.pugabyte.bearnation.server.features.achievements;

import me.pugabyte.bearnation.BNCore;
import me.pugabyte.bearnation.api.framework.annotations.Disabled;
import org.bukkit.event.Listener;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

public class Achievements {

	// TODO:
	// - Test DB implementation, Mongo may not like Object
	// - Update economy listener
	// - Update event listeners (see events folder)
	// - Add more achievements

	public Achievements() {
		new Reflections(getClass().getPackage().getName()).getSubTypesOf(Listener.class).forEach(listener -> {
			try {
				if (listener.getAnnotation(Disabled.class) == null)
					BNCore.registerListener(new ObjenesisStd().newInstance(listener));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

}
