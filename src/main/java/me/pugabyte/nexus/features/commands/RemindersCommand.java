package me.pugabyte.nexus.features.commands;

import joptsimple.internal.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.commands.RemindersCommand.ReminderConfig.Reminder;
import me.pugabyte.nexus.features.commands.RemindersCommand.ReminderConfig.Reminder.ReminderCondition;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.nexus.models.discord.DiscordService;
import me.pugabyte.nexus.models.discord.DiscordUser;
import me.pugabyte.nexus.models.mutemenu.MuteMenuUser;
import me.pugabyte.nexus.models.vote.VoteService;
import me.pugabyte.nexus.models.vote.VoteSite;
import me.pugabyte.nexus.models.vote.Voter;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.StringUtils.ellipsis;
import static me.pugabyte.nexus.utils.StringUtils.shortDateTimeFormat;

@NoArgsConstructor
@Aliases("reminder")
@Permission("group.seniorstaff")
public class RemindersCommand extends CustomCommand implements Listener {
	static {
		ConfigurationSerialization.registerClass(ReminderConfig.class, "ReminderConfig");
		ConfigurationSerialization.registerClass(Reminder.class, "Reminder");
	}

	private static final File file = Nexus.getFile("reminders.yml");
	private static YamlConfiguration yaml;
	private static ReminderConfig config;

	static {
		load();
	}

	public RemindersCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private void save() {
		try {
			yaml.set("config", config);
			yaml.save(file);
		} catch (Exception ex) {
			Nexus.severe("An error occurred while trying to write reminders configuration file: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void saveAndEdit(Reminder reminder) {
		save();
		edit(reminder);
	}

	@Path("reload")
	void reload() {
		load();
		send(PREFIX + "Reload complete");
	}

	private static void load() {
		yaml = YamlConfiguration.loadConfiguration(file);
		config = (ReminderConfig) yaml.get("config", new ReminderConfig());
		if (config == null) config = new ReminderConfig();
	}

	@Path("create <id> <text...>")
	void create(String id, String text) {
		config.add(Reminder.builder().id(id).text(text).build());
		save();
		send(PREFIX + "Reminder &e" + id + " &3created");
	}

	@Confirm
	@Path("delete <id>")
	void delete(Reminder reminder) {
		config.remove(reminder.getId());
		save();
		send(PREFIX + "Reminder &e" + reminder.getId() + " &cdeleted");
	}

	@Path("edit <id>")
	void edit(Reminder reminder) {
		send(json(PREFIX + "Edit reminder &e" + reminder.getId() + " &f &f ").group()
				.next("&6⟳").hover("&6Refresh").command("/reminders edit " + reminder.getId()).group()
				.next(" ").group()
				.next("&c✖").hover("&cDelete").command("/reminders delete " + reminder.getId()));

		line();
		send(json(" &3Text: &7" + reminder.getText()).hover("&3Click to edit").suggest("/reminders edit text " + reminder.getId() + " " + reminder.getText()));
		line();
		send(json(" &3Command: &7" + reminder.getCommand()).hover("&3Click to edit").suggest("/reminders edit command " + reminder.getId() + " " + (reminder.getCommand() == null ? "" : reminder.getCommand())));
		send(json(" &3Suggest: &7" + reminder.getSuggest()).hover("&3Click to edit").suggest("/reminders edit suggest " + reminder.getId() + " " + (reminder.getSuggest() == null ? "" : reminder.getSuggest())));
		send(json(" &3URL: &7" + reminder.getUrl()).hover("&3Click to edit").suggest("/reminders edit url " + reminder.getId() + " " + (reminder.getUrl() == null ? "" : reminder.getUrl())));
		JsonBuilder hover = json(" &3Hover: ");
		if (reminder.getHover().isEmpty())
			hover.next("&7null").hover("&3Click to add line").suggest("/reminders edit hover add " + reminder.getId() + " ");
		else {
			for (int i = 1; i <= reminder.getHover().size(); i++) {
				String line = reminder.getHover().get(i - 1);
				hover.newline().next(" &f &f ").group()
						.next("&c[-]").command("/reminders edit hover delete " + reminder.getId() + " " + i).hover("&cClick to delete").group()
						.next(" ").group()
						.next(line).suggest("/reminders edit hover set " + reminder.getId() + " " + i + " " + line).hover("&3Click to edit").group();
			}
		}

		send(hover);

		line();

		if (reminder.isEnabled())
			send(json(" " + StringUtils.CHECK + " Enabled").hover("&3Click to &cdisable").command("/reminders disable " + reminder.getId()));
		else
			send(json(" " + StringUtils.X + " Disabled").hover("&3Click to &aenable").command("/reminders enable " + reminder.getId()));

		if (reminder.isMotd())
			send(json(" &3Type: &eMOTD").hover("&3Click to toggle").command("/reminders edit motd " + reminder.getId() + " false"));
		else
			send(json(" &3Type: &3Reminder").hover("&3Click to toggle").command("/reminders edit motd " + reminder.getId() + " true"));

		send(json(" &3Show permissions ").group().next("&a[+]").hover("&aAdd permission").suggest("/reminders edit showPermissions add " + reminder.getId() + " "));
		showPermissionsEdit(reminder, reminder.getShowPermissions(), "showPermissions");

		send(json(" &3Hide permissions ").group().next("&a[+]").hover("&aAdd permission").suggest("/reminders edit hidePermissions add " + reminder.getId() + " "));
		showPermissionsEdit(reminder, reminder.getHidePermissions(), "hidePermissions");

		send(json(" &3Start time: &e" + (reminder.getStartTime() == null ? "None" : shortDateTimeFormat(reminder.getStartTime())))
				.hover("&3Click to edit").suggest("/reminders edit startTime " + reminder.getId() + " YYYY-MM-DDTHH:MM:SS"));
		send(json(" &3End time: &e" + (reminder.getEndTime() == null ? "None" : shortDateTimeFormat(reminder.getEndTime())))
				.hover("&3Click to edit").suggest("/reminders edit endTime " + reminder.getId() + " YYYY-MM-DDTHH:MM:SS"));

		send(json(" &3Condition: &e" + (reminder.getCondition() == null ? "None" : camelCase(reminder.getCondition())))
				.hover("&3Click to edit").suggest("/reminders edit condition " + reminder.getId() + " "));
		line();
	}

	private void showPermissionsEdit(Reminder reminder, Set<String> permissions, String command) {
		if (permissions.isEmpty())
			send("   &cNone");
		else
			for (String permission : permissions)
				send(json("   &c[-]").hover("&cRemove permission").command("/reminders edit " + command + " remove " + reminder.getId() + " " + permission).group().next(" &e" + permission));
	}

	@Path("enable <id>")
	void enable(Reminder reminder) {
		reminder.setEnabled(true);
		saveAndEdit(reminder);
	}

	@Path("disable <id>")
	void disable(Reminder reminder) {
		reminder.setEnabled(false);
		saveAndEdit(reminder);
	}

	@Path("edit id <id> <newId>")
	void editId(Reminder reminder, String newId) {
		reminder.setId(newId);
		saveAndEdit(reminder);
	}

	@Path("edit text <id> <text...>")
	void editText(Reminder reminder, String text) {
		reminder.setText(text);
		saveAndEdit(reminder);
	}

	@Path("edit hover add <id> <text...>")
	void editHoverAdd(Reminder reminder, String hover) {
		reminder.getHover().add(hover);
		saveAndEdit(reminder);
	}

	@Path("edit hover set <id> <line> <text...>")
	void editHoverSet(Reminder reminder, int line, String hover) {
		reminder.getHover().set(line - 1, hover);
		saveAndEdit(reminder);
	}

	@Path("edit hover delete <line> <id>")
	void editHoverRemove(Reminder reminder, int line) {
		reminder.getHover().remove(line - 1);
		saveAndEdit(reminder);
	}

	@Path("edit command <id> <text...>")
	void editCommand(Reminder reminder, String command) {
		reminder.setCommand(command);
		saveAndEdit(reminder);
	}

	@Path("edit suggest <id> <text...>")
	void editSuggest(Reminder reminder, String suggest) {
		reminder.setSuggest(suggest);
		saveAndEdit(reminder);
	}

	@Path("edit url <id> <text...>")
	void editUrl(Reminder reminder, String url) {
		reminder.setUrl(url);
		saveAndEdit(reminder);
	}

	@Path("edit motd <id> [enable]")
	void editMotd(Reminder reminder, Boolean enable) {
		if (enable == null)
			enable = !reminder.isMotd();
		reminder.setMotd(enable);
		saveAndEdit(reminder);
	}

	@Path("edit showPermissions add <id> <permission(s)>")
	void editShowPermissionsAdd(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		reminder.getShowPermissions().addAll(permissions);
		saveAndEdit(reminder);
	}

	@Path("edit showPermissions remove <id> <permission(s)>")
	void editShowPermissionsRemove(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		reminder.getShowPermissions().removeAll(permissions);
		saveAndEdit(reminder);
	}

	@Path("edit hidePermissions add <id> <permission(s)>")
	void editHidePermissionsAdd(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		reminder.getHidePermissions().addAll(permissions);
		saveAndEdit(reminder);
	}

	@Path("edit hidePermissions remove <id> <permission(s)>")
	void editHidePermissionsRemove(Reminder reminder, @Arg(type = String.class) List<String> permissions) {
		reminder.getHidePermissions().removeAll(permissions);
		saveAndEdit(reminder);
	}

	@Path("edit startTime <id> [time]")
	void editStartTime(Reminder reminder, LocalDateTime startTime) {
		reminder.setStartTime(startTime);
		saveAndEdit(reminder);
	}

	@Path("edit endTime <id> [time]")
	void editEndTime(Reminder reminder, LocalDateTime endTime) {
		reminder.setEndTime(endTime);
		saveAndEdit(reminder);
	}

	@Path("edit condition <id> [condition]")
	void editCondition(Reminder reminder, ReminderCondition condition) {
		reminder.setCondition(condition);
		saveAndEdit(reminder);
	}

	@Path("list [page]")
	void list(@Arg("1") int page) {
		if (config.getAllReminders().isEmpty())
			error("No reminders have been created");

		send(PREFIX + "Reminders");
		BiFunction<Reminder, Integer, JsonBuilder> formatter = (reminder, index) ->
				json("&3" + (index + 1) + " " + (reminder.isMotd() ? "&6➤" : "&b⚡") + " &e" + reminder.getId() + " &7- " + ellipsis(reminder.getText(), 50))
						.addHover("&3Type: &e" + (reminder.isMotd() ? "MOTD" : "Reminder"))
						.addHover("&7" + reminder.getText())
						.command("/reminders edit " + reminder.getId());
		paginate(config.getAllReminders(), formatter, "/reminders list", page);
	}

	@Path("show <player> <reminder>")
	void show(Player player, Reminder reminder) {
		reminder.send(player);
	}

	@Path("test <player> <reminder>")
	void testCriteria(Player player, Reminder reminder) {
		if (reminder.getCondition() == null)
			error("Reminder &e" + reminder.getId() + " &cdoes not have a condition so players will always receive it");

		send(PREFIX + player.getName() + " &ewould" + (reminder.getCondition().test(player) ? "" : " not")
				+ " &3receive the &e" + camelCase(reminder.getCondition()) + " &3reminder");
	}

	@ConverterFor(Reminder.class)
	Reminder convertToReminder(String value) {
		return config.findRequestMatch(value).orElseThrow(() -> new InvalidInputException("Reminder &e" + value +" &cnot found"));
	}

	@TabCompleterFor(Reminder.class)
	List<String> tabCompleteReminder(String filter) {
		return config.getAllReminders().stream()
				.map(Reminder::getId)
				.filter(request -> request.toLowerCase().startsWith(filter.toLowerCase()))
				.collect(Collectors.toList());
	}

	private static final int interval = Time.SECOND.x(30);

	static {
		Tasks.repeatAsync(interval, interval, () -> {
			if (true) return;

			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!PlayerUtils.isPuga(player))
					continue;

				Utils.attempt(100, () -> {
					Reminder reminder = config.getRandomReminder();

					if (!reminder.test(player))
						return false;

					reminder.send(player);
					return true;
				});
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		for (Reminder motd : config.getMotds())
			if (motd.test(event.getPlayer()))
				motd.send(event.getPlayer());
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@SerializableAs("ReminderConfig")
	public static class ReminderConfig implements ConfigurationSerializable {
		private List<Reminder> reminders = new ArrayList<>();

		public ReminderConfig(Map<String, Object> map) {
			this.reminders = (List<Reminder>) map.getOrDefault("reminders", reminders);
		}

		@Override
		public Map<String, Object> serialize() {
			return new LinkedHashMap<String, Object>() {{
				put("reminders", reminders);
			}};
		}

		public Optional<Reminder> findRequestMatch(String id) {
			return reminders.stream()
					.filter(_request -> _request.getId().equalsIgnoreCase(id))
					.findFirst();
		}

		public Reminder getRandomReminder() {
			return RandomUtils.randomElement(getReminders());
		}

		public List<Reminder> getAllReminders() {
			return reminders;
		}

		public List<Reminder> getReminders() {
			return reminders.stream().filter(reminder -> !reminder.isMotd()).collect(Collectors.toList());
		}

		public List<Reminder> getMotds() {
			return reminders.stream().filter(Reminder::isMotd).collect(Collectors.toList());
		}

		public void add(Reminder reminder) {
			if (findRequestMatch(reminder.getId()).isPresent())
				throw new InvalidInputException("An reminder with id &e" + reminder.getId() + " &calready exists");

			reminders.add(reminder);
		}

		public void remove(String id) {
			if (!findRequestMatch(id).isPresent())
				throw new InvalidInputException("Reminder with id &e" + id + " &cnot found");

			reminders.removeIf(reminder -> reminder.getId().equalsIgnoreCase(id));
		}

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		@SerializableAs("Reminder")
		public static class Reminder implements ConfigurationSerializable {
			private String id;
			private String text;
			private String command;
			private String suggest;
			private String url;
			@Builder.Default
			private List<String> hover = new ArrayList<>();
			@Builder.Default
			private boolean enabled = true;
			private boolean motd;
			@Builder.Default
			private Set<String> showPermissions = new HashSet<>();
			@Builder.Default
			private Set<String> hidePermissions = new HashSet<>();
			private LocalDateTime startTime;
			private LocalDateTime endTime;
			private ReminderCondition condition;

			@Getter
			private static final String PREFIX = "&8&l[&b⚡&8&l] &7";

			public Reminder(Map<String, Object> map) {
				this.id = (String) map.getOrDefault("id", id);
				this.text = (String) map.getOrDefault("text", text);
				this.command = (String) map.getOrDefault("command", command);
				this.suggest = (String) map.getOrDefault("suggest", suggest);
				this.url = (String) map.getOrDefault("url", url);
				this.hover = map.get("hover") != null ? (List<String>) map.get("hover") : new ArrayList<>();
				this.enabled = map.get("enabled") != null ? (boolean) map.get("enabled") : enabled;
				this.motd = map.get("motd") != null ? (boolean) map.get("motd") : motd;
				this.showPermissions = map.get("showPermissions") != null ? new HashSet<>((List<String>) map.get("showPermissions")) : new HashSet<>();
				this.hidePermissions = map.get("hidePermissions") != null ? new HashSet<>((List<String>) map.get("hidePermissions")) : new HashSet<>();
				this.startTime = map.get("startTime") != null ? new LocalDateTimeConverter().decode(map.getOrDefault("startTime", startTime)) : null;
				this.endTime = map.get("endTime") != null ? new LocalDateTimeConverter().decode(map.getOrDefault("endTime", endTime)) : null;
				try {
					this.condition = map.get("condition") != null ? ReminderCondition.valueOf((String) map.get("condition")) : null;
				} catch (IllegalArgumentException ex) {
					Nexus.log("Reminder Condition invalid for " + id + ": " + map.getOrDefault("condition", condition));
				}
			}

			@Override
			public Map<String, Object> serialize() {
				return new LinkedHashMap<String, Object>() {{
					put("id", id);
					put("text", text);
					put("command", command);
					put("suggest", suggest);
					put("url", url);
					put("hover", hover);
					put("enabled", enabled);
					put("motd", motd);
					put("showPermissions", new ArrayList<>(showPermissions));
					put("hidePermissions", new ArrayList<>(hidePermissions));
					put("startTime", new LocalDateTimeConverter().encode(startTime));
					put("endTime", new LocalDateTimeConverter().encode(endTime));
					put("condition", condition != null ? condition.name() : null);
				}};
			}

			public void send(Player player) {
				if (motd) {
					PlayerUtils.send(player, text);
				} else {
					if (MuteMenuUser.hasMuted(player, MuteMenuItem.REMINDERS))
						return;

					PlayerUtils.send(player, "");
					PlayerUtils.send(player, getJson());
					PlayerUtils.send(player, "");
				}
			}

			@NotNull
			private JsonBuilder getJson() {
				JsonBuilder json = new JsonBuilder(PREFIX + text);
				if (!Strings.isNullOrEmpty(command)) json.command(command);
				if (!Strings.isNullOrEmpty(suggest)) json.suggest(suggest);
				if (!Strings.isNullOrEmpty(url)) json.url(url);
				if (!hover.isEmpty()) json.hover(hover);
				return json;
			}

			public boolean test(Player player) {
				if (!enabled)
					return false;

				if (!showPermissions.isEmpty()) {
					boolean canSee = false;
					for (String showPermission : showPermissions)
						if (player.hasPermission(showPermission)) {
							canSee = true;
							break;
						}

					if (!canSee)
						return false;
				}

				if (!hidePermissions.isEmpty()) {
					boolean canHide = false;
					for (String hidePermission : hidePermissions)
						if (player.hasPermission(hidePermission)) {
							canHide = true;
							break;
						}

					if (canHide)
						return false;
				}

				if (startTime != null && startTime.isAfter(LocalDateTime.now()))
					return false;

				if (endTime != null && endTime.isBefore(LocalDateTime.now()))
					return false;

				if (condition != null && !condition.test(player))
					return false;

				return true;
			}

			@AllArgsConstructor
			public enum ReminderCondition {
				VOTE(player -> {
					Voter voter = new VoteService().get(player);
					return voter.getActiveVotes().size() < VoteSite.values().length - 2;
				}),
				DISCORD_LINK(player -> {
					DiscordUser user = new DiscordService().get(player);
					return user.getUserId() == null;
				});

				@Getter
				private final Predicate<Player> condition;

				public boolean test(Player player) {
					return condition.test(player);
				}
			}
		}

	}

}