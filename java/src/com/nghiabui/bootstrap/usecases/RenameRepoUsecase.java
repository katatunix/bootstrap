package com.nghiabui.bootstrap.usecases;

import com.nghiabui.common.AppException;

public class RenameRepoUsecase implements Id_String_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public RenameRepoUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId, String newName) {
		try {
			gateway.saveRepoName(repoId, newName);
			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
