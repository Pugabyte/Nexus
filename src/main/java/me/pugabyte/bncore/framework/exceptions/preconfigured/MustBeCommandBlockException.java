package me.pugabyte.bncore.framework.exceptions.preconfigured;

public class MustBeCommandBlockException extends PreConfiguredException {

	public MustBeCommandBlockException() {
		super("You must be console to use this command");
	}

}
