package me.pugabyte.bearnation.server.models.vote;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopVoter {
	private String uuid;
	private long count;
}
