package me.pugabyte.bearnation.api.framework.exceptions.preconfigured;

public class MustBeCommandBlockException extends PreConfiguredException {

	public MustBeCommandBlockException() {
		super("You must be a command block to use this command");
	}

}
