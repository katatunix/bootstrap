package com.nghiabui.bootstrap.usecases;

import com.nghiabui.common.AppException;

public class DeleteRepoUsecase implements Id_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public DeleteRepoUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId) {
		try {
			gateway.deleteRepo(repoId);

			for (int taskId : gateway.loadTaskIdsOfRepo(repoId)) {
				gateway.deleteTask(taskId);
			}

			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
