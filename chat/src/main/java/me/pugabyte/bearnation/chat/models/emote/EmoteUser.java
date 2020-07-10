package me.pugabyte.bearnation.chat.models.emote;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bearnation.api.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bearnation.api.framework.persistence.service.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("emote_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class EmoteUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;

}
