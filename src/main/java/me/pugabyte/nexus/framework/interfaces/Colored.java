package me.pugabyte.nexus.framework.interfaces;

import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

import static me.pugabyte.nexus.utils.StringUtils.toHex;

public interface Colored {
	/**
	 * Returns the color corresponding to this object.
	 * @return an RGB color
	 */
	@NotNull Color getColor();

	/**
	 * Returns the chat color corresponding to this object.
	 */
	default @NotNull ChatColor getChatColor() {
		return ChatColor.of(getColor());
	}

	/**
	 * Returns the Adventure text color corresponding to this object.
	 */
	default @NotNull TextColor getTextColor() {
		return TextColor.color(getColor().getRGB());
	}

	/**
	 * Returns the Bukkit color corresponding to this object.
	 */
	default @NotNull org.bukkit.Color getBukkitColor() {
		return org.bukkit.Color.fromRGB(getColor().getRGB());
	}

	/**
	 * Returns the Minecraft hex string of the color corresponding to this object, as defined by {@link #getChatColor()}.
	 * @return a string like "&#ABCDEF"
	 */
	default @NotNull String getHex() {
		return "&" + toHex(getChatColor());
	}
}