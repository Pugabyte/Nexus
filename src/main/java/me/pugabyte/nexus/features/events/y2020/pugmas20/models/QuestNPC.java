package me.pugabyte.nexus.features.events.y2020.pugmas20.models;

import lombok.Getter;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.models.Script;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.models.Merchants.MerchantNPC;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.GiftGiver;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.LightTheTree;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.OrnamentVendor.Ornament;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Quests;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.TheMines;
import me.pugabyte.nexus.models.eventuser.EventUser;
import me.pugabyte.nexus.models.eventuser.EventUserService;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
public enum QuestNPC {
	ELF1(3078) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service pugmasService = new Pugmas20Service();
			Pugmas20User pugmasUser = pugmasService.get(player);

			switch (pugmasUser.getLightTreeStage()) {
				case STARTED:
					pugmasUser.setLightTreeStage(QuestStage.STEP_ONE);
					pugmasService.save(pugmasUser);

					return Arrays.asList(
							Script.wait(getGreeting()),

							Script.wait(5, "I can’t find the Ceremonial Lighter… Ooooh Santa is gonna be SO mad at me if I’ve lost it."),

							Script.wait(5, "We haven't used it since last year. Could you search in the basement to help me find it?")
					);
				case STEP_ONE:
					return Arrays.asList(
							Script.wait("Did you find the Ceremonial Lighter in the basement?")
					);
				case STEP_TWO:
					if (!hasItem(player, LightTheTree.lighter_broken)) {
						return Arrays.asList(
								Script.wait("Did you find the Ceremonial Lighter in the basement?")
						);
					}

					return Arrays.asList(
							Script.wait("The mechanism is broken! How could I have been so careless."),

							Script.wait(5, "The ceremony is supposed to start soon, but there might be enough time- " +
									"hurry to the tree and find " + ELF2.getName() + " they will know how to fix this.")
					);

				case STEPS_DONE:
					return Arrays.asList(
							Script.wait("You have it! Just in the nick of time. The ceremony shall now begin."),

							Script.wait(5, "Light all the torches around Santa’s Workshop leading up to the tree using the ceremonial lighter, " +
									"don’t forget the one at the base of the tree! You will be timed, so hurry!")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(getThanks())
					);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	},
	ELF2(3079) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			switch (user.getLightTreeStage()) {
				case NOT_STARTED:
					user.setLightTreeStage(QuestStage.STARTED);
					user.getNextStepNPCs().add(ELF1.getId());
					service.save(user);

					return Arrays.asList(
							Script.wait(getGreeting()),

							Script.wait(5, "It's time for our annual tree lighting ceremony, but " + ELF1.getName() +
									" still hasn’t returned with the Ceremonial Lighter!"),

							Script.wait(5, "Would you mind finding him for me? He should be in the workshop.")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait("Have you found " + ELF1.getName() + "? He should be in the workshop.")
					);

				case STEP_TWO:
					if (!hasItem(player, LightTheTree.lighter_broken)) {
						return Arrays.asList(
								Script.wait("Did you find " + ELF1.getName() + " in the workshop?")
						);
					}

					user.setLightTreeStage(QuestStage.STEP_THREE);
					user.getNextStepNPCs().add(FORELF.getId());
					service.save(user);

					return Arrays.asList(
							Script.wait("Dangit " + ELF1.getName() + ", I *told* him to be careful with this."),

							Script.wait(5, "Hmm, yes, it is fixable, just needs a new flint wheel and a steel striker."),

							Script.wait(5, "If you get me a piece of flint and a steel ingot, I can make both fast."),

							Script.wait(5, "Head to the coal mine and you should be able to get both- ask the " + FORELF.getName() + " for help.")
					);
				case STEP_THREE:
					ItemStack lighter = getItem(player, LightTheTree.lighter_broken);
					ItemStack steelIngot = getItem(player, LightTheTree.steel_ingot);
					ItemStack flint = getItem(player, TheMines.getFlint());
					if (lighter == null || steelIngot == null || flint == null) {
						return Arrays.asList(
								Script.wait("In order to fix the Ceremonial Lighter, I need a piece of flint and a steel ingot.")
						);
					}

					player.getInventory().removeItem(lighter, steelIngot, flint);
					ItemUtils.giveItem(player, LightTheTree.lighter);

					user.setLightTreeStage(QuestStage.STEPS_DONE);
					service.save(user);

					return Arrays.asList(
							Script.wait("There you go, right as rain. Now give this back to " +
									ELF1.getName() + " and tell him to be careful with it this time!")
					);
				case STEPS_DONE:
					return Arrays.asList(
							Script.wait("Have you returned the Ceremonial Lighter to " + ELF1.getName() + "?")
					);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	},
	FORELF(3080) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			if (user.getLightTreeStage() == QuestStage.STEP_THREE) {
				return Arrays.asList(
						Script.wait("Eh? What? Oh, right, lemme take the ear plugs out."),

						Script.wait(5, "Sorry, we don’t have any flint or steel available right now but " +
								"you can certainly go grab your own."),

						Script.wait(5, "Grab a sieve and a pick from this equipment stand. You will need " +
								"to sift the gravel piles for flint."),

						Script.wait(5, "For the steel you will need the blacksmiths help, " +
								"he'll make the steel for you, if you give him the required coal and iron."),

						Script.wait(5, "His workshop is located in the Plaza District.")
				);
			}

			switch (user.getMinesStage()) {
				case NOT_STARTED:
					user.setMinesStage(QuestStage.STARTED);
					service.save(user);

					return Arrays.asList(
							Script.wait("Since you’re already cleared for the mine, wanna do me a favor?"),

							Script.wait(5, "There's always a rush of last minute demands for materials by the workshop- " +
									"things that need to be fixed, production that came up a little short."),

							Script.wait(5, "And almost all my mine-elves have been sent to help in the wrapping and sled loading."),

							Script.wait(5, "If you bring me ingots and put them in this crate here, I’ll see you get paid.")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait("If you bring me ingots and put them in this crate here, I’ll see you get paid.")
					);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	},
	QA_ELF(3081) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			EventUserService eventUserService = new EventUserService();
			EventUser eventUser = eventUserService.get(player);

			switch (user.getToyTestingStage()) {
				case NOT_STARTED:
					user.setToyTestingStage(QuestStage.STARTED);
					service.save(user);

					return Arrays.asList(
							Script.wait(getGreeting()),

							Script.wait(5, "Hey kid, I need a favor! After last year’s debacle with the sled, " +
									"half of the Quality Assurance team was fired, and the other half have spent all year " +
									"on making sure that the Sled won’t fall apart again."),

							Script.wait(5, "But that's left just me to try and keep up with testing all the toys " +
									"that come off the line. I’m way, way behind and Pugmas is coming fast."),

							Script.wait(5, "Think you can help an elf out? You might need to find a friend to help you, " +
									"the games on the table there need to be tested before they can be added to the present piles."),

							Script.wait(5, "If you could just play a round or two of each, that would be perfect.")
					);
				case STARTED:
					return Arrays.asList(
							Script.wait("You still need to test " + getUnplayedToys(user))
					);
				case STEPS_DONE:
					user.setToyTestingStage(QuestStage.COMPLETE);
					user.getNextStepNPCs().remove(getId());
					service.save(user);

					Tasks.wait(0, () -> {
						eventUser.giveTokens(300);
						eventUserService.save(eventUser);
						eventUser.send(Pugmas20.PREFIX + " You have received 300 Event Tokens!");
					});

					return Arrays.asList(
							Script.wait("They all work! Excellent! You have the thanks of many children and one overworked elf."),

							Script.wait(5, "Here, have this...")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(getThanks())
					);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	},
	ELF3(3082) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			EventUserService eventUserService = new EventUserService();
			EventUser eventUser = eventUserService.get(player);

			switch (user.getOrnamentVendorStage()) {
				case NOT_STARTED:
					user.setOrnamentVendorStage(QuestStage.STARTED);
					user.getNextStepNPCs().add(MerchantNPC.ORNAMENT_VENDOR.getNpcId());
					service.save(user);

					return Arrays.asList(
							Script.wait(getGreeting()),

							Script.wait(5, "This tree is so big it takes a lot of ornaments to fill, " +
									"and I may have uh, lost some of them from last year."),

							Script.wait(5, "Don’t tell Santa! Just help me out. Here in town is an ornament vendor, " +
									"he trades different wood types that we need for the factory for spare pugmas ornaments."),

							Script.wait(5, "I’d just ask him for some extra myself, but he’s mean and would tell Santa I " +
									"lost the town’s ornaments. If you bring me one of each of the 10 ornaments, I'll reward you."),

							Script.wait(5, "Find the LumberJack in the orchid, he can help you out with obtaining the necessary logs.")
					);
				case STARTED:
					List<ItemStack> ornaments = OrnamentVendor.getOrnaments(player);

					if (ornaments.size() != Ornament.values().length) {
						return Arrays.asList(
								Script.wait("If you trade with the ornament vendor, and bring me one of each of the 10 ornaments, I'll reward you.")
						);
					}

					user.setOrnamentVendorStage(QuestStage.COMPLETE);
					user.getNextStepNPCs().remove(getId());
					service.save(user);

					for (ItemStack ornament : ornaments)
						player.getInventory().removeItem(ornament);

					Tasks.wait(Time.SECOND, () -> {
						eventUser.giveTokens(300);
						eventUserService.save(eventUser);
						eventUser.send(Pugmas20.PREFIX + " You have received 300 Event Tokens!");
					});

					return Arrays.asList(
							Script.wait("I am so grateful, thank you! Here, for all the trouble, have this...")
					);
				case COMPLETE:
					return Arrays.asList(
							Script.wait(getThanks())
					);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	},
	LUMBERJACK(3108) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			if (user.getLightTreeStage().equals(QuestStage.STARTED)) {
				return Arrays.asList(
						Script.wait(getGreeting()),

						Script.wait(5, "So you need some logs huh? We'll you're in luck, the soil that this orchid was built on is magical, and the trees grow back in only a few minutes."),

						Script.wait(5, "So grab an extra axe from my workshop and start choppin' down some trees!")
				);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	},
	GIFT_GIVER(3110) {
		@Override
		public List<Script> getScript(Player player) {
			Pugmas20Service service = new Pugmas20Service();
			Pugmas20User user = service.get(player);

			if (user.getGiftGiverStage() == QuestStage.NOT_STARTED) {
				user.setGiftGiverStage(QuestStage.COMPLETE);
				user.getNextStepNPCs().remove(getId());
				service.save(user);

				GiftGiver.giveGift(player);

				return Arrays.asList(
						Script.wait("Spread some cheer and give this gift to another player!")
				);
			}

			return Arrays.asList(
					Script.wait(getGreeting())
			);
		}
	};

	@NotNull
	public static String getUnplayedToys(Pugmas20User user) {
		List<String> leftover = new ArrayList<>();
		if (!user.isMasterMind())
			leftover.add("MasterMind");
		if (!user.isBattleship())
			leftover.add("Battleship");
		if (!user.isConnectFour())
			leftover.add("Connect4");
		if (!user.isTicTacToe())
			leftover.add("TicTacToe");
		return String.join(", ", leftover);
	}

	@Getter
	int id;

	QuestNPC(int id) {
		this.id = id;
	}

	public static QuestNPC getById(int id) {
		for (QuestNPC value : QuestNPC.values())
			if (value.id == id) return value;
		return null;
	}

	public static void startScript(Player player, int id) {
		QuestNPC npc = QuestNPC.getById(id);
		if (npc != null)
			npc.sendScript(player);
	}

	public void sendScript(Player player) {
		List<Script> scripts = getScript(player);
		if (scripts == null || scripts.isEmpty()) return;

		AtomicInteger wait = new AtomicInteger(0);

		AtomicReference<String> npcName = new AtomicReference<>("");
		getName(npcName);

		scripts.forEach(script -> {
			wait.getAndAdd(script.getDelay());

			script.getLines().forEach(line -> {
				line = line.replaceAll("<player>", player.getName());
				if (line.contains("<self>")) {
					npcName.set("&b&lYOU&f");
					line = line.replaceAll("<self> ", "");
				}

				String message = "&3" + npcName.get() + " &7> &f" + line;
				Tasks.wait(wait.get(), () -> {
					PlayerUtils.send(player, message);
					Quests.sound_npcAlert(player);
				});
			});
		});
	}

	public abstract List<Script> getScript(Player player);

	public String getName() {
		return getName(new AtomicReference<>(""));
	}

	private String getName(AtomicReference<String> npcName) {
		if (npcName == null)
			npcName = new AtomicReference<>("");

		if (this == QA_ELF)
			npcName.set("Q.A. Elf");
		else
			npcName.set(camelCase(name()));
		npcName.set(npcName.get().replaceAll("[0-9]+", ""));

		return npcName.get();
	}

	public boolean hasItem(Player player, ItemStack item) {
		return player.getInventory().containsAtLeast(item, 1);
	}

	public ItemStack getItem(Player player, ItemStack item) {
		for (ItemStack content : player.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(content))
				continue;

			if (ItemUtils.isFuzzyMatch(item, content))
				return content;
		}
		return null;
	}

	private static String getGreeting() {
		List<String> greetings = Arrays.asList(
				"Happy holidays!",
				"Yuletide greetings!",
				"Season’s greetings!",
				"Happy New Year!",
				"Merry Pugmas!");

		return RandomUtils.randomElement(greetings);
	}

	private static String getThanks() {
		List<String> thanks = Arrays.asList(
				"Thanks again for the help!",
				"I appreciate your help.",
				"I am grateful for your assistance.");

		return RandomUtils.randomElement(thanks);
	}
}
