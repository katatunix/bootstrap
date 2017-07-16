package com.nghiabui.bootstrap.usecases;

import com.nghiabui.common.AppException;

import java.util.HashSet;

public class CreateRepoUsecase {

	private final Gateway gateway;
	private final CreateOut out;

	public CreateRepoUsecase(Gateway gateway, CreateOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	public void execute(String repoName) {
		try {
			final int repoId = gateway.newRepoId();
			gateway.saveRepoName(repoId, repoName);
			gateway.saveRepoContent(repoId, new HashSet<>());

			out.createSuccess(repoId);
		} catch (AppException e) {
			out.createError(e.getMessage());
		}
	}
	
}
