package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Param;
import com.nghiabui.bootstrap.core.Pool;
import com.nghiabui.bootstrap.core.RelationCache;
import com.nghiabui.common.AppException;

public class CreateTaskUsecase implements Id_String_Usecase {

	private final Gateway gateway;
	private final CreateOut out;

	public CreateTaskUsecase(Gateway gateway, CreateOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId, String taskName) {
		try {
			if (!isRepoExisted(repoId)) {
				out.createError("Not found the repo with id " + repoId);
				return;
			}
			
			final int taskId = gateway.newTaskId();

			gateway.saveRepoIdOfTask(taskId, repoId);
			gateway.saveTaskName(taskId, taskName);

			gateway.savePatternPool(taskId, new Pool<>());
			gateway.saveWordPool(taskId, new Pool());

			gateway.savePatternCache(taskId, new RelationCache<>());
			gateway.saveWordCache(taskId, new RelationCache<>());
			gateway.saveParam(taskId, new Param(10, 0.7, 5, 60));
			
			out.createSuccess(taskId);
		} catch (AppException e) {
			out.createError(e.getMessage());
		}
	}
	
	private boolean isRepoExisted(int repoId) {
		return gateway.loadAllRepoNames().containsKey(repoId);
	}

}
