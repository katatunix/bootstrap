package com.nghiabui.bootstrap.usecases;

import com.nghiabui.common.AppException;

public class RenameTaskUsecase implements Id_String_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public RenameTaskUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int taskId, String newName) {
		try {
			gateway.saveTaskName(taskId, newName);
			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
