package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;

@Aliases({"ase", "armourstandeditor"})
public class ArmorStandEditorCommand extends CustomCommand {

	public ArmorStandEditorCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("arms [enable]")
	void arms(Boolean enabled) {
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(armorStand.getLocation()))
			error("You cannot edit armor stands here");

		if (enabled == null)
			enabled = !armorStand.hasArms();

		armorStand.setArms(enabled);
		send(PREFIX + "Arms " + (enabled ? "&aenabled" : "&cdisabled"));
	}

	@Path("summon 0")
	@Permission("group.admin")
	void summon0() {
		final ArmorStand armorStand = location().getWorld().spawn(LocationUtils.getCenteredLocation(location()), ArmorStand.class);
		armorStand.setRightArmPose(EulerAngle.ZERO);
		armorStand.setLeftArmPose(EulerAngle.ZERO);
		armorStand.setHeadPose(EulerAngle.ZERO);
		armorStand.setInvulnerable(true);
		armorStand.setGravity(false);
		armorStand.setBasePlate(false);
		armorStand.setArms(true);
		armorStand.setDisabledSlots(EquipmentSlot.values());
	}

	@Path("position arms left")
	@Permission("group.admin")
	void position_arms() {
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(armorStand.getLocation()))
			error("You cannot edit armor stands here");

		float yaw = LocationUtils.normalizeYaw(location());
		float pitch = location().getPitch();

		line();
		send("yaw: " + yaw + " / pitch: " + pitch);

		double x;
		if (yaw > 180)
			x = yaw + pitch;
		else
			x = yaw - pitch;

		double y = yaw + 180;
		if (y > 360)
			y -= 360;
		double z = 0;

		double xr = Math.toRadians(x);
		double yr = Math.toRadians(y);
		double zr = Math.toRadians(z);

		send(StringUtils.getDf().format(x) + " " + StringUtils.getDf().format(y) + " " + StringUtils.getDf().format(z));
		send(StringUtils.getDf().format(xr) + " " + StringUtils.getDf().format(yr) + " " + StringUtils.getDf().format(zr));
		EulerAngle ea = new EulerAngle(xr, yr, zr);
		armorStand.setLeftArmPose(ea);
	}

	@Path("set arms left <x> <y> <z>")
	@Permission("group.admin")
	void set_arms_left(float x, float y, float z) {
		position_arms();
		ArmorStand armorStand = (ArmorStand) getTargetEntityRequired(EntityType.ARMOR_STAND);

		if (!isPerkAllowedAt(armorStand.getLocation()))
			error("You cannot edit armor stands here");

		EulerAngle ea = new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
		armorStand.setLeftArmPose(ea);
	}



}
