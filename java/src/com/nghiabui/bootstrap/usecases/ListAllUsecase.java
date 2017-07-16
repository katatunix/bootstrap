package com.nghiabui.bootstrap.usecases;

import com.nghiabui.common.AppException;
import com.nghiabui.common.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListAllUsecase {

	private final Gateway gateway;
	private final ListAllOut out;

	public ListAllUsecase(Gateway gateway, ListAllOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	public void execute() {
		try {
			gateway.loadAllRepoNames().forEach((repoId, repoName) -> {
				out.listAllSuccess_Repo(repoId, repoName);
				
				final List<Tuple<Integer, String>> tasks = new ArrayList<>();
				
				for (int taskId : gateway.loadTaskIdsOfRepo(repoId)) {
					final String taskName = gateway.loadTaskName(taskId);
					tasks.add(new Tuple<>(taskId, taskName));
				}
				
				tasks.sort(Comparator.comparing(tuple -> tuple.y));
				tasks.forEach(tuple -> out.listAllSuccess_Task(tuple.x, tuple.y));
			});
		} catch (AppException e) {
			out.listAllError(e.getMessage());
		}
	}

}
