package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.usecases.CreateOut;

public class CreatePresenter implements CreateOut {
	
	private final Console console;
	
	public CreatePresenter(Console console) {
		this.console = console;
	}
	
	@Override
	public void createSuccess(int id) {
		console.infoln("Created with id = " + id);
	}
	
	@Override
	public void createError(String message) {
		console.errorln(message);
	}
	
}
