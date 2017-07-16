package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.core.Log;
import com.nghiabui.bootstrap.usecases.ViewRepoOut;

import java.util.Collection;

public class ViewRepoPresenter implements ViewRepoOut {
	
	private final Console console;
	
	public ViewRepoPresenter(Console console) {
		this.console = console;
	}
	
	@Override
	public void viewRepoSuccess(String name, Collection<Log> logs) {
		console.infoln("Repo name: " + name);
		console.infoln("Length: " + logs.size() + " lines");
		for (Log log : logs) {
			console.infoln(log.toString());
		}
	}
	
	@Override
	public void viewRepoError(String message) {
		console.errorln(message);
	}
	
}
