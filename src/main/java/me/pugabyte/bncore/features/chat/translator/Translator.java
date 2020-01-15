package me.pugabyte.bncore.features.chat.translator;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.chat.herochat.HerochatAPI;
import me.pugabyte.bncore.skript.SkriptFunctions;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Translator implements Listener {

	public Translator() {
		BNCore.registerListener(this);
	}

	public HashMap<UUID, ArrayList<UUID>> translatorMap = new HashMap<>();

	public String apiKey = BNCore.getInstance().getConfig().getConfigurationSection("yandex").getString("apiKey");
	public TranslatorHandler translatorHandler = new TranslatorHandler(apiKey);

	String PREFIX = Utils.getPrefix("Translator");

	@EventHandler
	public void onChat(ChannelChatEvent event) {
		if (!event.getResult().toString().equals("ALLOWED")) return;
		Player sender = event.getSender().getPlayer();
		List<Chatter> chatters = HerochatAPI.getRecipients(event.getSender(), event.getChannel());

		Utils.wait(1, () -> {
			try {
				if (!translatorMap.containsKey(sender.getUniqueId())) return;

				translatorHandler.getLanguage(event.getMessage()).thenAcceptAsync(language -> {
					if (language == Language.EN) return;

					translatorHandler.translate(event.getMessage(), language, Language.EN).thenAcceptAsync(translated -> {
						for (UUID uuid : translatorMap.get(sender.getUniqueId())) {
							Player translating = Utils.getPlayer(uuid).getPlayer();

							if (uuid == sender.getUniqueId()) continue;
							if (!chatters.contains(Herochat.getChatterManager().getChatter(translating))) continue;

							SkriptFunctions.json(translating, PREFIX + sender.getName() + " &e(&3" + language.name() +
									"&e) &3&l> &7" + translated + "||ttp:" + language.getName());
						}
					});
				});
			} catch (Exception ex) {
				ex.printStackTrace();
				for (UUID uuid : translatorMap.get(sender.getUniqueId())) {
					Player translating = Utils.getPlayer(uuid).getPlayer();
					translating.sendMessage(Utils.colorize(PREFIX + "Failed to translate message from " + event.getSender().getPlayer().getDisplayName() + "."));
				}
			}
		});
	}

	@EventHandler
	public void onTranslatedDisconnect(PlayerQuitEvent event) {
		if (!translatorMap.containsKey(event.getPlayer().getUniqueId())) return;
		for (UUID uuid : translatorMap.get(event.getPlayer().getUniqueId()))
			Utils.getPlayer(uuid).getPlayer().sendMessage(PREFIX + event.getPlayer().getDisplayName() + " has logged out. Disabling translation.");

		translatorMap.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onTranslatorDisconnect(PlayerQuitEvent event) {
		translatorMap.keySet().forEach(uuid -> translatorMap.get(uuid).remove(event.getPlayer().getUniqueId()));
	}

}
