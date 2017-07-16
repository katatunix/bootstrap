package com.nghiabui.bootstrap.presentation;

import com.nghiabui.bootstrap.presentation.presenters.Console;

public class ConsoleImpl implements Console {
	
	private final com.nghiabui.common.Console console;
	
	public ConsoleImpl(com.nghiabui.common.Console console) {
		this.console = console;
	}
	
	@Override
	public Console info(String str) {
		console.info(str);
		return this;
	}

	@Override
	public Console info(int tabNum, String str) {
		console.info(tabNum, str);
		return this;
	}

	@Override
	public Console infoln() {
		console.infoln();
		return this;
	}
	
	@Override
	public Console infoln(String line) {
		console.infoln(line);
		return this;
	}
	
	@Override
	public Console infoln(int tabNum, String line) {
		console.infoln(tabNum, line);
		return this;
	}
	
	@Override
	public Console errorln(String line) {
		console.errorln(line);
		return this;
	}

}
