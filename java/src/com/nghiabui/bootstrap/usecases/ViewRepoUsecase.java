package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Log;
import com.nghiabui.common.AppException;

import java.util.Set;

public class ViewRepoUsecase implements Id_Usecase {

	private final Gateway gateway;
	private final ViewRepoOut out;

	public ViewRepoUsecase(Gateway gateway, ViewRepoOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId) {
		try {
			final String name = gateway.loadRepoName(repoId);
			final Set<Log> logs = gateway.loadRepoContent(repoId);
			out.viewRepoSuccess(name, logs);
		} catch (AppException e) {
			out.viewRepoError(e.getMessage());
		}
	}

}
