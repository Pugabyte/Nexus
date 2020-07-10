package me.pugabyte.bearnation.server.features.commands;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bearnation.BNCore;
import me.pugabyte.bearnation.api.framework.commands.models.CustomCommand;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Arg;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.HideFromHelp;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.Path;
import me.pugabyte.bearnation.api.framework.commands.models.annotations.TabCompleteIgnore;
import me.pugabyte.bearnation.api.framework.commands.models.events.CommandEvent;
import me.pugabyte.bearnation.api.models.cooldown.CooldownService;
import me.pugabyte.bearnation.api.models.nerd.Nerd;
import me.pugabyte.bearnation.api.models.nerd.NerdService;
import me.pugabyte.bearnation.api.utils.JsonBuilder;
import me.pugabyte.bearnation.api.utils.Tasks;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.features.menus.BookBuilder.WrittenBookMenu;
import me.pugabyte.bearnation.server.models.referral.Referral;
import me.pugabyte.bearnation.server.models.referral.Referral.Origin;
import me.pugabyte.bearnation.server.models.referral.ReferralService;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public class ReferralCommand extends CustomCommand implements Listener {
	private final ReferralService service = new ReferralService();
	private Referral referral;

	public ReferralCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayer())
			referral = service.get(player());
	}

	@Path
	void run() {
		JsonBuilder json = new JsonBuilder().next("&6Choose one:").newline();
		for (Referral.Origin origin : Referral.Origin.values())
			json.next("&3" + origin.getDisplay())
					.hover("&e" + origin.getLink())
					.command("/referral choose " + origin.name().toLowerCase())
					.group()
					.newline();

		new WrittenBookMenu().addPage(json).open(player());
	}

	@HideFromHelp
	@TabCompleteIgnore
	@Path("choose [origin]")
	void choose(Origin origin) {
		referral.setOrigin(origin);
		service.save(referral);
		if (origin == Origin.OTHER) {

// In case this ever works in the future
//			new EditableBookMenu()
//					.addPage(json("Tell use more:").newline())
//					.onSign(meta -> {
//						referral.setExtra(meta.getPage(0));
//						service.save(referral);
//					})
//					.open(player());

			BNCore.getSignMenuFactory().blank()
					.response(lines -> {
						List<String> list = new ArrayList<>(Arrays.asList(lines));
						list.removeIf(Strings::isNullOrEmpty);
						referral.setExtra(String.join(" ", list));
						service.save(referral);
						send(PREFIX + "Thank you for your feedback!");
					})
					.open(player());
		} else {
			send(PREFIX + "Thank you for your feedback!");
		}
	}

	@Path("debug [player]")
	void debug(@Arg("self") OfflinePlayer player) {
		Referral referral = service.get(player);
		send(referral.toString());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.waitAsync(Time.MINUTE, () -> {
			Referral referral = new ReferralService().get(event.getPlayer());
			if (referral.getOrigin() == null) {
				Nerd nerd = new NerdService().get(event.getPlayer());
				if (nerd.getFirstJoin().isAfter(LocalDateTime.now().minusHours(2))) {
					Tasks.sync(() -> {
						if (!event.getPlayer().isOnline())
							return;

						if (new CooldownService().check(event.getPlayer(), "referralAsk", Time.MINUTE.x(5))) {
							send(event.getPlayer(), json().newline()
									.next("&e&lHey there! &3Could you quickly tell us where you found this server? &eClick here!")
									.command("/referral")
									.newline());
						}
					});
				}
			}
		});
	}

}
