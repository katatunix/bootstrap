package com.nghiabui.common.io;

import com.nghiabui.common.AppException;
import com.nghiabui.common.System;

public class FileWriter implements AutoCloseable {

	private final java.io.FileWriter writer;

	public FileWriter(String path) {
		try {
			writer = new java.io.FileWriter(path);
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
	public FileWriter write(String str) {
		try {
			writer.write(str);
		} catch (Exception e) {
			throw new AppException(e);
		}
		return this;
	}

	public FileWriter writeln(String line) {
		try {
			writer.write(line + System.NEW_LINE);
		} catch (Exception e) {
			throw new AppException(e);
		}
		return this;
	}

	public FileWriter writeln() {
		try {
			writer.write(System.NEW_LINE);
		} catch (Exception e) {
			throw new AppException(e);
		}
		return this;
	}

	@Override
	public void close() {
		try {
			writer.close();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

}
