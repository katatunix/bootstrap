package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.usecases.PrintUsageOut;

public class PrintUsagePresenter implements PrintUsageOut {
	
	private final Console console;
	
	public PrintUsagePresenter(Console console) {
		this.console = console;
	}
	
	@Override
	public PrintUsageOut println(String line) {
		console.infoln(line);
		return this;
	}
	
	@Override
	public PrintUsageOut println(int tabNum, String line) {
		console.infoln(tabNum, line);
		return this;
	}
	
	@Override
	public PrintUsageOut println() {
		console.infoln();
		return this;
	}
	
}
