package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Log;

import java.util.Collection;

public interface ViewRepoOut {

	void viewRepoSuccess(String name, Collection<Log> logs);
	void viewRepoError(String message);

}
