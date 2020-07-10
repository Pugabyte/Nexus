package me.pugabyte.bearnation.api.framework.exceptions.postconfigured;

import me.pugabyte.bearnation.api.framework.exceptions.preconfigured.PreConfiguredException;

public class PlayerNotFoundException extends PreConfiguredException {

	public PlayerNotFoundException(String input) {
		super("Player &e" + input + " &cnot found");
	}

}