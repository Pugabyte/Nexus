package me.pugabyte.nexus.features.regionapi.events.npc;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.Getter;
import me.pugabyte.nexus.features.regionapi.MovementType;
import me.pugabyte.nexus.features.regionapi.events.common.EnteringRegionEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.event.Event;

/**
 * Event that is triggered before a npc enters a WorldGuard region, can be cancelled sometimes
 */
@Getter(AccessLevel.PUBLIC)
public class NPCEnteringRegionEvent extends EnteringRegionEvent {
	protected final NPC npc;

	/**
	 * Creates a new NPCEnteringRegionEvent
	 *
	 * @param region       the region the npc is entering
	 * @param npc          the npc who triggered the event
	 * @param movementType the type of movement how the npc enters the region
	 */
	public NPCEnteringRegionEvent(ProtectedRegion region, NPC npc, MovementType movementType, Event parent) {
		super(region, npc.getEntity(), movementType, parent);
		this.npc = npc;
	}

}
