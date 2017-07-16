package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.usecases.ListAllOut;

public class ListAllPresenter implements ListAllOut {
	
	private final Console console;
	
	public ListAllPresenter(Console console) {
		this.console = console;
	}
	
	@Override
	public void listAllSuccess_Repo(int repoId, String name) {
		console.infoln(String.format("Repo: [%d] %s", repoId, name));
	}
	
	@Override
	public void listAllSuccess_Task(int taskId, String name) {
		console.infoln(1, String.format("Task: [%d] %s", taskId, name));
	}
	
	@Override
	public void listAllError(String message) {
		console.errorln(message);
	}
	
}
