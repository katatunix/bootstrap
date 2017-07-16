package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Pool;
import com.nghiabui.common.AppException;

public class ClearWordsUsecase implements Id_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public ClearWordsUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int taskId) {
		try {
			gateway.saveWordPool(taskId, new Pool<>());
			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
