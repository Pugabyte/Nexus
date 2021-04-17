package me.pugabyte.nexus.features.events.y2021.bearfair21.commands;

import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolf;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.MiniGolfUtils;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus.MiniGolfColorMenu;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.menus.MiniGolfParticleMenu;
import me.pugabyte.nexus.features.events.y2021.bearfair21.fairgrounds.minigolf.models.MiniGolfHole;
import me.pugabyte.nexus.features.menus.BookBuilder;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21User;
import me.pugabyte.nexus.models.bearfair21.MiniGolf21UserService;
import me.pugabyte.nexus.utils.JsonBuilder;

import java.util.Map;

@Permission("group.staff")
public class MiniGolfCommand extends CustomCommand {
	private MiniGolf21User user;
	private final MiniGolf21UserService service = new MiniGolf21UserService();

	public MiniGolfCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = MiniGolfUtils.getUser(uuid());
	}

	@Path("play")
	void play() {
		if (user.isPlaying())
			error("You are already playing");

		user.setPlaying(true);
		user.getScore().clear();
		service.save(user);

		player().setCollidable(false);

		MiniGolf.takeKit(user);
		MiniGolf.giveKit(user);
		send(PREFIX + "You are now playing");
	}

	@Path("quit")
	void quit() {
		if (!user.isPlaying())
			error("You are not playing");

		user.setPlaying(false);
		service.save(user);

		if (user.getSnowball() != null) {
			user.getSnowball().remove();
			user.setSnowball(null);
		}

		player().setCollidable(true);

		MiniGolf.takeKit(user);
		send(PREFIX + "You have quit playing");
	}

	@Path("score <page>")
	void getScore(int page) {
		BookBuilder.WrittenBookMenu builder = new BookBuilder.WrittenBookMenu();
		JsonBuilder json = new JsonBuilder();
		Map<MiniGolfHole, Integer> score = user.getScore();

		json.next(" ## |  Par | Strokes").newline();
		json.next("------------------").newline();

		int count = 0;
		int holeNdx = page == 1 ? 1 : 10;

		for (MiniGolfHole hole : MiniGolfHole.getHoles()) {
			if (hole.ordinal() < holeNdx - 1) {
				continue;
			}

			String holeNumber = String.valueOf(holeNdx);
			if (holeNdx < 10)
				holeNumber = "0" + holeNdx;

			String strokes = " ?";
			if (score.containsKey(hole)) {
				int strokeCount = score.get(hole);
				strokes = " " + strokeCount;
				if (strokeCount > 9)
					strokes = String.valueOf(strokeCount);
			}

			json.next(" " + holeNumber + " |   " + hole.getPar() + "   |   " + strokes).newline();

			holeNdx++;
			if (++count >= 9)
				break;
		}

		json.newline().group();

		if (page == 1)
			json.next("      &3----> ").hover("&eNext").command("/minigolf score 2");
		else
			json.next("      &3<---- ").hover("&eBack").command("/minigolf score 1");

		builder.addPage(json).open(player());
	}

	@Path("kit")
	void getKit() {
		MiniGolf.takeKit(user);
		MiniGolf.giveKit(user);
	}

	@Path("color")
	void color() {
		MiniGolfColorMenu.getInv().open(player());
	}

	@Path("particle")
	void particle() {
		MiniGolfParticleMenu.getInv().open(player());
	}

	@Path("clearDatabase")
	@Confirm
	void resetData() {
		service.clearCache();
		service.deleteAll();
		service.clearCache();
	}
}
