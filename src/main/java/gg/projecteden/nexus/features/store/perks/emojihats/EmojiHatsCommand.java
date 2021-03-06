package gg.projecteden.nexus.features.store.perks.emojihats;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Permission("emojihats.use")
public class EmojiHatsCommand extends CustomCommand {

	public EmojiHatsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		EmojiHat.init();
	}

	@Path("<type>")
	void run(EmojiHat type) {
		if (!type.canBeUsedBy(player()))
			error("You do not have permission for this emoji hat");

		type.run(player());
	}

	@Path("run <player> <type>")
	@Permission("group.admin")
	void run(Player player, EmojiHat type) {
		type.run(player);
	}

	@Path("getFrameItems <type>")
	@Permission("group.admin")
	void getFrameItems(EmojiHat type) {
		PlayerUtils.giveItems(player(), type.getFrameItems());
	}

	@TabCompleterFor(EmojiHat.class)
	List<String> tabCompleterForEmojiHat(String filter) {
		return Arrays.stream(EmojiHat.values())
				.filter(type -> type.canBeUsedBy(player()))
				.map(Enum::name)
				.map(String::toLowerCase)
				.toList();
	}

}
