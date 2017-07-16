package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Param;
import com.nghiabui.common.AppException;

public class UpdateParamUsecase {

	private final Gateway gateway;
	private final BasicOut out;

	public UpdateParamUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	public void execute(int taskId, Param param) {
		try {
			gateway.saveParam(taskId, param);
			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
