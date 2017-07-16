package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.usecases.BasicOut;

public class BasicPresenter implements BasicOut {

	private final Console console;

	public BasicPresenter(Console console) {
		this.console = console;
	}

	@Override
	public void success() {
		console.infoln("Successful");
	}

	@Override
	public void error(String message) {
		console.errorln(message);
	}
	
}
