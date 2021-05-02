package me.pugabyte.nexus.models.skincache;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.gson.Gson;
import dev.dbassett.skullcreator.SkullCreator;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.LocalDateTimeConverter;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.exceptions.NexusException;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static eden.utils.StringUtils.isNullOrEmpty;
import static eden.utils.StringUtils.uuidUnformat;

@Data
@Builder
@Entity("skin_cache")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocalDateTimeConverter.class})
public class SkinCache implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String value;
	private String signature;
	private LocalDateTime timestamp;

	public static SkinCache of(String name) {
		return of(PlayerUtils.getPlayer(name));
	}

	public static SkinCache of(PlayerOwnedObject player) {
		return of(player.getUuid());
	}

	public static SkinCache of(OfflinePlayer player) {
		return of(player.getUniqueId());
	}

	public static SkinCache of(UUID uuid) {
		return new SkinCacheService().get(uuid);
	}

	public boolean isCached() {
		return !isNullOrEmpty(value);
	}

	public ItemStack getHead() {
		if (!isCached())
			update();

		if (!isCached())
			return getHeadFromPlayer();

		return new ItemBuilder(SkullCreator.itemFromBase64(value)).name(getNickname() + "'s Head").build();
	}

	public SkullMeta getHeadMeta() {
		return (SkullMeta) getHead().getItemMeta();
	}

	public boolean update() {
		String previous = this.value;
		ProfileProperty property;
		try {
			property = getCurrentProperty();
		} catch (NexusException ex) {
			Nexus.warn("Error getting " + getNickname() + "'s current player profile: " + ex.getMessage());
			return false;
		}

		this.timestamp = LocalDateTime.now();
		this.value = property.getValue();
		this.signature = property.getSignature();
		new SkinCacheService().save(this);

		return this.value.equals(previous);
	}

	@Data
	private static class FakePlayerProfile {
		private String id;
		private String name;
		private List<ProfileProperty> properties;

		private UUID getUuid() {
			return UUID.fromString(StringUtils.uuidFormat(id));
		}

		PlayerProfile getPlayerProfile() {
			PlayerProfile profile = Bukkit.getServer().createProfile(getUuid(), name);
			profile.setProperties(properties);
			return profile;
		}

		private static String URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

		@SneakyThrows
		static Response call(UUID uuid) {
			String url = String.format(URL, uuidUnformat(uuid.toString()));
			Request request = new Request.Builder().url(url).build();
			return new OkHttpClient().newCall(request).execute();
		}
	}

	@SneakyThrows
	private @NotNull ProfileProperty getCurrentProperty() {
		if (isOnline())
			return getTextureProperty(getPlayer().getPlayerProfile());

		Response response = FakePlayerProfile.call(uuid);

		if (response.body() != null) {
			String body = response.body().string();
			FakePlayerProfile fakeProfile = new Gson().fromJson(body, FakePlayerProfile.class);
			if (fakeProfile == null)
				throw new NexusException("Response body could not be mapped to a player profile object: " + body);
			return getTextureProperty(fakeProfile.getPlayerProfile());
		}

		throw new NexusException("Response body from is null");
	}

	private @NotNull ProfileProperty getTextureProperty(PlayerProfile profile) {
		if (profile != null)
			for (ProfileProperty property : profile.getProperties())
				if (property.getName().equals("textures"))
					return property;

		throw new NexusException("No texture property:\n" + StringUtils.toPrettyString(profile));
	}

	private ItemStack getHeadFromPlayer() {
		return new ItemBuilder(Material.PLAYER_HEAD).skullOwnerActual(getOfflinePlayer()).build();
	}

}