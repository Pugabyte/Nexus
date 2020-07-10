package me.pugabyte.bearnation.server.features.holidays.pride20;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.cooldown.CooldownService;
import me.pugabyte.bearnation.api.models.setting.Setting;
import me.pugabyte.bearnation.api.models.setting.SettingService;
import me.pugabyte.bearnation.api.utils.ItemBuilder;
import me.pugabyte.bearnation.api.utils.StringUtils;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.api.utils.Utils;
import me.pugabyte.bearnation.api.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

@NoArgsConstructor
public class Pride20Command extends CustomCommand implements Listener {

	SettingService service = new SettingService();

	public Pride20Command(CommandEvent event) {
		super(event);
	}

	@Path("parade join [player]")
	void joinParade(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		if (!player().hasPermission("group.staff"))
			player = player();

		Setting setting = service.get(player, "pride20Parade");
		if (setting.getBoolean())
			error("You have already joined the parade");

		WorldGuardUtils wgUtils = new WorldGuardUtils(Bukkit.getWorld("safepvp"));
		ProtectedRegion region = wgUtils.getProtectedRegion("pride20_parade");
		Location npcLoc;
		if (!wgUtils.getPlayersInRegion("pride20_parade").contains(player)) {
			Location random;
			int attempts = 0;
			do {
				random = Bukkit.getWorld("safepvp").getHighestBlockAt(wgUtils.getRandomBlock(region).getLocation()).getLocation();
				attempts++;
				if (attempts >= 300) {
					error("There was an error while trying to join the parade, please try again");
					break;
				}
			} while (!region.contains(wgUtils.toBlockVector3(random)) || citizenAtBlock(random) || !random.getBlock().getType().isSolid() || !isHighestBlock(random));
			npcLoc = random;
		} else {
			npcLoc = player.getPlayer().getLocation();
		}
		npcLoc.setYaw(180);
		npcLoc.setPitch(0);

		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, player.getName());
		npc.spawn(npcLoc.add(0, 2, 0));

		npcLoc = Utils.getCenteredLocation(npcLoc);
		npc.teleport(npcLoc, PlayerTeleportEvent.TeleportCause.COMMAND);

		setting.setBoolean(true);
		service.save(setting);
		send(PREFIX + "You have joined the pride parade");
	}

	@Path("parade leave [player]")
	void leaveParade(@Arg("self") OfflinePlayer player) {
		if (!player().hasPermission("group.staff"))
			player = player();

		Setting setting = service.get(player, "pride20Parade");
		if (!setting.getBoolean())
			error("You have not joined the parade");

		WorldGuardUtils wgUtils = new WorldGuardUtils(Bukkit.getWorld("safepvp"));
		ProtectedRegion region = wgUtils.getProtectedRegion("pride20_parade");
		for (Entity entity : wgUtils.getEntitiesInRegion(Bukkit.getWorld("safepvp"), "pride20_parade")) {
			if (!entity.hasMetadata("NPC")) continue;
			if (entity.getName().equalsIgnoreCase(player.getName()))
				CitizensAPI.getNPCRegistry().getNPC(entity).destroy();
		}

		setting.setBoolean(false);
		service.save(setting);
		send(PREFIX + "You have left the pride parade");
	}

	public boolean isHighestBlock(Location loc) {
		for (int i = 1; i < 20; i++) {
			if (loc.clone().add(0, i, 0).getBlock().getType() != Material.AIR)
				return false;
		}
		return true;
	}

	private boolean citizenAtBlock(Location loc) {
		for (Entity entity : loc.getNearbyEntities(1, 2, 1)) {
			if (entity.hasMetadata("NPC"))
				return true;
		}
		return false;
	}

	@EventHandler
	public void onBalloonNPCClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != 2771) return;
		Player player = event.getClicker();

		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(player, "prideDyeBomb", Time.MINUTE.x(1)))
			return;

		player.sendMessage(StringUtils.colorize("&3Vendor > &eSadly all my balloons have uh... floated away, but I can give you this to play with"));
		DyeBombCommand.giveDyeBomb(player, 5);
	}

	@EventHandler
	public void onSecretCatClick(NPCRightClickEvent event) {
		if (event.getNPC().getId() != 2776) return;
		Player player = event.getClicker();

		SettingService service = new SettingService();
		Setting setting = service.get(player, "pride20Secret");
		if (setting.getBoolean()) {

			CooldownService cooldownService = new CooldownService();
			if (!cooldownService.check(player, "pride20Cat", Time.SECOND.x(10)))
				return;

			player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 5f, .08f);
			return;
		}

		player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, 5f, .08f);
		Utils.giveItem(player, new ItemBuilder(Material.ORANGE_BANNER)
				.pattern(DyeColor.RED, PatternType.STRIPE_TOP)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_MIDDLE)
				.pattern(DyeColor.LIME, PatternType.HALF_HORIZONTAL_MIRROR)
				.pattern(DyeColor.YELLOW, PatternType.STRIPE_MIDDLE)
				.pattern(DyeColor.BLUE, PatternType.STRIPE_BOTTOM)
				.build());

		setting.setBoolean(true);
		service.save(setting);
		player.sendMessage(StringUtils.colorize("&eHow did you even get here? I mean.... meow"));
	}


	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase("pride20")) return;
		CooldownService cooldownService = new CooldownService();
		if (!cooldownService.check(event.getPlayer(), "pride20enter", Time.MINUTE.x(5)))
			return;
		event.getPlayer().sendMessage(StringUtils.colorize("&eWelcome to the Pride Parade!" +
				" &3Have a look at all the colorful floats and roam around the city. If you'd like to join the parade, " +
				"type &c/pride20 parade join &3while standing where you want to be in the parade. &eEnjoy and happy pride!"));
	}


}
