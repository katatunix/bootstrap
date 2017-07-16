package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.presentation.presenters.Console;
import com.nghiabui.common.AppException;
import com.nghiabui.common.io.FileReader;

import java.util.Collection;
import java.util.Optional;

public class Utils {

	private final Console console;

	public Utils(Console console) {
		this.console = console;
	}

	public boolean checkArgLen(boolean condition) {
		if (!condition) {
			console.errorln("Invalid number of arguments");
			return false;
		}
		return true;
	}

	public Optional<Integer> convertId(String idStr) {
		return convertStr2Int(idStr, "Invalid format of id");
	}

	public Optional<Integer> convertStr2Int(String str, String errorMessage) {
		try {
			return Optional.of(Integer.valueOf(str));
		} catch (NumberFormatException e) {
			console.errorln(errorMessage);
			return Optional.empty();
		}
	}

	public Optional<Collection<String>> readInputFile(String path) {
		final Collection<String> lines;
		try {
			lines = FileReader.readAllLines(path);
		} catch (AppException e) {
			console.errorln("Could not read file: " + path);
			return Optional.empty();
		}
		return Optional.of(lines);
	}

}
