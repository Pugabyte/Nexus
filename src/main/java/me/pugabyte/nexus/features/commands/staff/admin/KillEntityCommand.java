package me.pugabyte.nexus.features.commands.staff.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.pugabyte.nexus.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.utils.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Permission("group.seniorstaff")
@Aliases({"killall", "mobkill", "butcher", "killentities"})
public class KillEntityCommand extends CustomCommand {

	public KillEntityCommand(CommandEvent event) {
		super(event);
	}

	@Getter
	@AllArgsConstructor
	public enum KillableEntityGroup {
		LIVING(LivingEntity.class),
		HOSTILE(Monster.class, EntityUtils.getExtraHostileMobs());

		private final Class<? extends Entity> entityClass;
		private final List<EntityType> extraEntityTypes;

		KillableEntityGroup(Class<? extends Entity> entityClass) {
			this(entityClass, null);
		}

		public List<EntityType> getApplicableEntityTypes() {
			List<EntityType> applicable = new ArrayList<>();
			for (EntityType entityType : EntityType.values())
				if (entityType.getEntityClass() != null && this.entityClass.isAssignableFrom(entityType.getEntityClass()))
					applicable.add(entityType);

			if (extraEntityTypes != null && !extraEntityTypes.isEmpty())
				applicable.addAll(extraEntityTypes);

			return applicable;
		}
	}

	@Getter
	public static class KillEntityArg {
		private EntityType entityType;
		private KillableEntityGroup group;

		public KillEntityArg(EntityType entityType) {
			this.entityType = entityType;
		}

		public KillEntityArg(KillableEntityGroup group) {
			this.group = group;
		}

		public List<EntityType> getApplicableEntityTypes() {
			if (entityType != null)
				return Collections.singletonList(entityType);
			else if (group != null)
				return group.getApplicableEntityTypes();
			else
				return new ArrayList<>();
		}
	}

	@Path("<entityType> <radius>")
	void spawnEntity(@Arg(type = KillEntityArg.class) List<KillEntityArg> killEntityArg, double radius) {
		if (!isAdmin() && radius > 200)
			error("Radius cannot be greater than 200");

		List<EntityType> toKill = new ArrayList<>();
		for (KillEntityArg arg : killEntityArg)
			toKill.addAll(arg.getApplicableEntityTypes());
		toKill.remove(EntityType.PLAYER);

		Runnable kill = () -> {
			Set<Entity> entities = EntityUtils.getNearbyEntities(player().getLocation(), radius).keySet()
					.stream().filter(entity -> toKill.contains(entity.getType()))
					.collect(Collectors.toSet());
			entities.forEach(Entity::remove);
			send(PREFIX + "Killed " + entities.size() + " entities");
		};

		if (toKill.contains(EntityType.ARMOR_STAND) || toKill.contains(EntityType.ITEM_FRAME))
			ConfirmationMenu.builder()
				.onConfirm(e -> kill.run())
				.open(player());
		else
			kill.run();
	}

	@TabCompleterFor(KillEntityArg.class)
	List<String> tabCompleteKillableEntity(String value) {
		return new ArrayList<String>() {{
			addAll(tabCompleteLivingEntity(value));
			addAll(tabCompleteEnum(value, KillableEntityGroup.class));
			remove(EntityType.PLAYER.name());
		}};
	}

	@ConverterFor(KillEntityArg.class)
	KillEntityArg convertToKillableEntity(String value) {
		try {
			return new KillEntityArg((EntityType) convertToEnum(value, EntityType.class));
		} catch (InvalidInputException ex) {
			try {
				return new KillEntityArg((KillableEntityGroup) convertToEnum(value, KillableEntityGroup.class));
			} catch (InvalidInputException ex2) {
				throw new InvalidInputException("Could not convert " + value + " to a killable entity");
			}
		}
	}
}