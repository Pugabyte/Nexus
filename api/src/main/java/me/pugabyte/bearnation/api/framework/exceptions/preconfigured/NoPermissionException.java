package me.pugabyte.bearnation.api.framework.exceptions.preconfigured;

public class NoPermissionException extends PreConfiguredException {
	public NoPermissionException() {
		super("You don't have permission to do that!");
	}

}
