package me.pugabyte.bearnation.chat.models.discord;

import com.vdurmont.emoji.EmojiManager;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bearnation.api.BNCore;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.LocalDateTimeConverter;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bearnation.api.framework.persistence.service.PlayerOwnedObject;
import me.pugabyte.bearnation.api.models.task.Task;
import me.pugabyte.bearnation.api.models.task.TaskService;
import me.pugabyte.bearnation.api.utils.Time;
import me.pugabyte.bearnation.chat.Chat;
import me.pugabyte.bearnation.chat.features.discord.Bot;
import me.pugabyte.bearnation.chat.features.discord.Discord;
import me.pugabyte.bearnation.chat.features.discord.DiscordId.Role;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("discord_captcha")
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class DiscordCaptcha extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, LocalDateTime> confirmed = new HashMap<>();
	private Map<String, LocalDateTime> unconfirmed = new HashMap<>();

	private static final String taskId = "discord-unconfirmed-kick";

	public void require(String id) {
		unconfirmed.put(id, LocalDateTime.now());

		User user = Bot.KODA.jda().getUserById(id);
		if (user == null) {
			BNCore.warn("[Captcha] Cannot send verification message to null user");
		} else {
			user.openPrivateChannel().complete()
					.sendMessage("Please react to verify your account").complete()
					.addReaction(EmojiManager.getForAlias("thumbsup").getUnicode()).queue();
		}

		new TaskService(Chat.inst()).save(new Task(taskId, new HashMap<String, Object>() {{
			put("id", id);
		}}, LocalDateTime.now().plusMinutes(9)));
	}


	public void confirm(String id) {
		unconfirmed.remove(id);
		confirmed.put(id, LocalDateTime.now());
		Discord.addRole(id, Role.NERD);

		User user = Bot.KODA.jda().getUserById(id);
		String name = id;
		if (user != null)
			name = user.getName();

		Discord.staffLog("**[Captcha]** " + name + " - Completed verification");
	}

	public CaptchaResult check(String id) {
		if (confirmed.containsKey(id))
			return CaptchaResult.CONFIRMED;
		else if (unconfirmed.containsKey(id))
			return CaptchaResult.UNCONFIRMED;

		return CaptchaResult.NEW;
	}

	public enum CaptchaResult {
		CONFIRMED,
		UNCONFIRMED,
		NEW
	}

	static {
		Chat.tasks().repeatAsync(Time.SECOND, Time.SECOND.x(15), () -> {
			TaskService service = new TaskService(Chat.inst());
			service.process(taskId).forEach(task -> {
				try {
					Map<String, Object> data = task.getJson();
					String id = (String) data.get("id");
					String name = Discord.getName(id);

					DiscordCaptcha verification = new DiscordCaptchaService(Chat.inst()).get();
					CaptchaResult result = verification.check(id);
					if (result != CaptchaResult.CONFIRMED) {
						Member member = Discord.getGuild().getMemberById(id);

						if (member != null) {
							Discord.staffLog("**[Captcha]** " + name + " - Kicking");
							member.kick("Please complete the verification process in your DMs with KodaBear").queue();
						} else
							Chat.log("[Captcha] Kick scheduled for " + name + " cancelled, member not found");
					}
				} catch (Exception ex) {
					try {
						Discord.staffLog("**[Captcha]** Error in kick processor: " + ex.getMessage());
						ex.printStackTrace();
					} catch (Exception ex2) {
						ex2.printStackTrace();
					}
				}
				service.complete(task);
			});
		});
	}

}
