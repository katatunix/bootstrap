package com.nghiabui.common.io;

import com.nghiabui.common.AppException;

import java.io.BufferedReader;
import java.util.List;

public class FileReader {

	public static List<String> readAllLines(String path) {
		try (java.io.FileReader fr = new java.io.FileReader(path);
		     BufferedReader br = new BufferedReader(fr)) {
			return new AllLineReader(br).read();
		} catch (Exception e) {
			throw new AppException(e);
		}
	}

}
