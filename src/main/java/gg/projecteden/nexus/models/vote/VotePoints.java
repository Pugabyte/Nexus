package gg.projecteden.nexus.models.vote;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity(value = "vote_points", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class VotePoints implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private int points;

	public void takePoints(int points) {
		setPoints(getPoints() - points);
	}

	public void givePoints(int points) {
		setPoints(getPoints() + points);
	}

	public void setPoints(int points) {
		if (points < 0)
			throw new InvalidInputException("You do not have enough vote points");

		this.points = points;
	}

}
