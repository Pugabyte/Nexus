package me.pugabyte.bearnation.server.features.listeners;

import me.pugabyte.bearnation.BNCore;
import me.pugabyte.bearnation.api.framework.annotations.Disabled;
import org.bukkit.event.Listener;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

public class Listeners {

	public Listeners() {
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