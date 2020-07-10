package me.pugabyte.bearnation.chat.models.discord;

import me.pugabyte.bearnation.api.framework.persistence.annotations.PlayerClass;
import me.pugabyte.bearnation.api.framework.persistence.service.MongoService;
import me.pugabyte.bearnation.api.models.geoip.GeoIP;
import me.pugabyte.bearnation.api.utils.Utils;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

@PlayerClass(DiscordCaptcha.class)
public class DiscordCaptchaService extends MongoService {

	public DiscordCaptchaService(Plugin plugin) {
		super(plugin);
	}

	@Deprecated
	public Map<UUID, GeoIP> getCache() {
		return null;
	}

	@Override
	@NotNull
	@Deprecated // Use get()
	public <T> T get(UUID uuid) {
		throw new UnsupportedOperationException("Use get()");
	}

	public static DiscordCaptcha captcha;

	// Just a single object, tying it all to Koda's account
	public DiscordCaptcha get() {
		if (captcha == null) {
			captcha = database.createQuery(DiscordCaptcha.class).first();
			if (captcha == null)
				captcha = new DiscordCaptcha(Utils.getPlayer("KodaBear").getUniqueId());
		}

		return captcha;
	}

	@Override
	public <T> void saveSync(T object) {
		database.delete(object);
		database.save(object);
	}

}
