package me.pugabyte.bearnation.minigames.features.utils.damagetracker;

import me.pugabyte.bearnation.minigames.Minigames;
import me.pugabyte.bearnation.minigames.features.utils.damagetracker.models.DamageEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageTrackerListener implements Listener {

	public DamageTrackerListener() {
		Minigames.registerListener(this);
	}

	@EventHandler
	public void onDamage(DamageEvent event) {
		DamageTracker.log(event);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Entity damager = null;
		new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event).callEvent();

	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Entity damager;
		if (event.getDamager() instanceof Projectile) {
			damager = (Entity) ((Projectile) event.getDamager()).getShooter();
		} else {
			damager = event.getDamager();
		}

		new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event).callEvent();

	}

	@EventHandler
	public void onDamage(EntityDamageByBlockEvent event) {
		if (ignore(event))
			return;

		LivingEntity entity = (LivingEntity) event.getEntity();
		Block damager = event.getDamager();

		new DamageEvent(entity, damager, event.getCause(), event.getDamage(), event).callEvent();
	}

	boolean ignore(EntityDamageEvent event) {
		if (event.isCancelled())
			return true;
		return !(event.getEntity() instanceof LivingEntity);
	}

}
