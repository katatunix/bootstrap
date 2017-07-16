package com.nghiabui.common.io;

import com.nghiabui.common.AppException;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

public class AllLineReader {
	
	private final BufferedReader bufferedReader;
	
	public AllLineReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}
	
	public List<String> read() {
		try {
			final List<String> lines = new ArrayList<>();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			return lines;
		} catch (Exception e) {
			throw new AppException(e);
		}
	}
	
}
