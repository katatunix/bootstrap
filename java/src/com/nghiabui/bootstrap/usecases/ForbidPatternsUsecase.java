package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Pattern;
import com.nghiabui.bootstrap.core.Pool;
import com.nghiabui.common.AppException;

import java.util.Collection;

public class ForbidPatternsUsecase implements Id_Strings_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public ForbidPatternsUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int taskId, Collection<String> patterns) {
		try {
			try {
				final Pool<Pattern> pool = gateway.loadPatternPool(taskId);
				for (String p : patterns) {
					pool.addForbidden(new Pattern(p));
				}
				gateway.savePatternPool(taskId, pool);
				out.success();
			} catch (AppException e) {
				out.error(e.getMessage());
			}
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
