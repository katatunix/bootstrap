package com.nghiabui.common.io;

import com.nghiabui.common.AppException;

import java.util.ArrayList;
import java.util.Collection;

public class Folder {

	private final java.io.File folder;

	public Folder(String path) {
		this(new java.io.File(path));
	}

	public Folder(java.io.File folder) {
		this.folder = folder;
	}

	public String path() {
		return folder.getPath();
	}

	public void createIfNotExist() {
		if (folder.exists()) return;
		if (!folder.mkdirs()) {
			throw new AppException("Could not create folder \"" + path() + "\"");
		}
	}

	public Collection<File> childFiles() {
		final java.io.File[] children = folder.listFiles();
		if (children == null) {
			throw new AppException("Could not get child files of folder \"" + path() + "\"");
		}
		final Collection<File> files = new ArrayList<>();
		for (java.io.File f : children) {
			files.add(new File(f));
		}
		return files;
	}

	public File findChildFile(String fileName) {
		for (File file : childFiles()) {
			if (file.name().equals(fileName)) {
				return file;
			}
		}
		throw new AppException(String.format(
			"Could not find the file \"%s\" in the folder \"%s\"", fileName, path()
		));
	}

	public File getChildFile(String fileName) {
		return new File(new java.io.File(folder, fileName));
	}

	public void deleteChildFile(String fileName) {
		findChildFile(fileName).delete();
	}

}
