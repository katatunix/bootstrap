package com.nghiabui.bootstrap.presentation.controllers;

import com.nghiabui.bootstrap.presentation.presenters.Console;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class UtilsTest {

	@Test
	public void whenArgLenIsCorrect() {
		SpyConsole spyConsole = new SpyConsole();
		Utils util = new Utils(spyConsole);

		util.checkArgLen(true);

		assertNull(spyConsole.lastInfoStr);
		assertNull(spyConsole.lastErrorStr);
	}

	@Test
	public void whenArgLenIsIncorrect() {
		SpyConsole spyConsole = new SpyConsole();
		Utils util = new Utils(spyConsole);

		util.checkArgLen(false);

		assertNull(spyConsole.lastInfoStr);
		assertEquals("Invalid number of arguments", spyConsole.lastErrorStr);
	}

	@Test
	public void testConvert_WhenInputStringIsGood() {
		final SpyConsole spyConsole = new SpyConsole();
		final Utils util = new Utils(spyConsole);
		final String GOOD_STRING = "777";

		final Optional<Integer> result = util.convertStr2Int(GOOD_STRING, "haha");

		assertEquals(777, result.get().intValue());
		assertNull(spyConsole.lastInfoStr);
		assertNull(spyConsole.lastErrorStr);
	}

	@Test
	public void testConvert_WhenInputStringIsBad_ZeroIsReturned_AndErrorMesasge() {
		final SpyConsole spyConsole = new SpyConsole();
		final Utils util = new Utils(spyConsole);
		final String BAD_STRING = "hello";

		final Optional<Integer> result = util.convertStr2Int(BAD_STRING, "This is an error");
		assertFalse(result.isPresent());
		assertNull(spyConsole.lastInfoStr);
		assertEquals("This is an error", spyConsole.lastErrorStr);
	}

	@Test
	public void testReadFile_WhenInputFileIsGood() {
		final SpyConsole spyConsole = new SpyConsole();
		final Utils util = new Utils(spyConsole);

		util.readInputFile("test/res/UtilsTest/good.txt");

		assertNull(spyConsole.lastInfoStr);
		assertNull(spyConsole.lastErrorStr);
	}

	@Test
	public void testReadFile_WhenInputFileIsBad() {
		final SpyConsole spyConsole = new SpyConsole();
		final Utils util = new Utils(spyConsole);

		final String FILE_NAME = "none.txt";
		util.readInputFile(FILE_NAME);

		assertNull(spyConsole.lastInfoStr);
		assertEquals("Could not read file: " + FILE_NAME, spyConsole.lastErrorStr);
	}
	
}

class SpyConsole implements Console {
	String lastInfoStr = null;
	String lastErrorStr = null;
	
	@Override
	public Console info(String str) {
		return null;
	}

	@Override
	public Console info(int tabNum, String str) {
		return null;
	}

	public Console infoln(String str) {
		lastInfoStr = str;
		return this;
	}
	
	@Override
	public Console infoln(int tabNum, String line) {
		return null;
	}
	
	@Override
	public Console infoln() {
		return null;
	}
	
	public Console errorln(String str) {
		lastErrorStr = str;
		return this;
	}
}
