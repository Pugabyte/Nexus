package me.pugabyte.nexus.features.regionapi.events.common;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.abstraction.CancellableRegionEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a entity enters a WorldGuard region, can be cancelled sometimes
 */
public class EnteringRegionEvent extends CancellableRegionEvent {

	/**
	 * Creates a new EntityEnteringRegionEvent
	 *
	 * @param region       the region the entity is entering
	 * @param entity       the entity who triggered this event
	 * @param movementType the type of movement how the entity enters the region
	 * @param parentEvent  the event that triggered this event
	 */
	public EnteringRegionEvent(ProtectedRegion region, Entity entity, MovementType movementType, Event parentEvent) {
		super(region, entity, movementType, parentEvent);
	}

}
