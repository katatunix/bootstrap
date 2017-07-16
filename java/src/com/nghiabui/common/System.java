package com.nghiabui.common;

import java.util.Optional;

public class System {

	public final static String OS_NAME = java.lang.System.getProperty("os.name").toLowerCase();

	public final static boolean IS_UNIX =
		OS_NAME.contains("nix") ||
		OS_NAME.contains("nux") ||
		OS_NAME.contains("mac");

	public final static boolean IS_WINDOWS = OS_NAME.contains("win");

	public final static String NEW_LINE = java.lang.System.getProperty("line.separator");

	public static Optional<String> getEnv(String name) {
		final String value = java.lang.System.getenv(name);
		return value == null ? Optional.empty() : Optional.of(value);
	}

}
