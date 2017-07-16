package com.nghiabui.common.io;

import com.nghiabui.common.AppException;

import java.io.File;

public class FolderCreator {

	public static void create(String folder) {
		final File f = new File(folder);
		if (f.exists()) return;
		if (!f.mkdirs()) {
			throw new AppException("Could not create folder: " + folder);
		}
	}

}
