package com.nghiabui.bootstrap.usecases;

import com.nghiabui.common.AppException;

public class DeleteTaskUsecase implements Id_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public DeleteTaskUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int taskId) {
		try {
			gateway.deleteTask(taskId);
			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
