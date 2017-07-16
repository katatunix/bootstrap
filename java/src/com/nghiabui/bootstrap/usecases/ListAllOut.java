package com.nghiabui.bootstrap.usecases;

public interface ListAllOut {

	void listAllSuccess_Repo(int repoId, String name);
	void listAllSuccess_Task(int taskId, String name);

	void listAllError(String message);

}
