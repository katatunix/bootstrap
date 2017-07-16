package com.nghiabui.common.io;

import com.nghiabui.common.AppException;

public class File {

	private final java.io.File file;

	public File(String path) {
		this(new java.io.File(path));
	}

	public File(java.io.File file) {
		this.file = file;
	}

	public String name() {
		return file.getName();
	}

	public String baseName() {
		final String full = name();
		final int i = full.lastIndexOf('.');
		return i == -1 ? full : full.substring(0, i);
	}

	public String ext() {
		final String full = name();
		final int i = full.lastIndexOf('.');
		return i == -1 ? "" : full.substring(i + 1);
	}

	public String path() {
		return file.getPath();
	}

	public void delete() {
		if (!file.exists()) return;
		if (!file.delete()) {
			throw new AppException("Could not delete file " + path());
		}
	}

}
