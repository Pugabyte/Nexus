package me.pugabyte.nexus.models.radio;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.radio.RadioConfig.Radio;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Entity("radio_user")
@Converters({UUIDConverter.class})
public class RadioUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;

	private boolean mute = false;

	private String serverRadioId;
	private String lastServerRadioId;

	@Embedded
	private Set<String> leftRadiusRadios = new HashSet<>();

	public void setServerRadioId(String serverRadioId) {
		this.lastServerRadioId = this.serverRadioId;
		this.serverRadioId = serverRadioId;
	}

	public Radio getServerRadio() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get(Nexus.getUUID0());
		return config.getById(serverRadioId);
	}

	public Radio getLastServerRadio() {
		RadioConfigService configService = new RadioConfigService();
		RadioConfig config = configService.get(Nexus.getUUID0());
		return config.getById(lastServerRadioId);
	}
}