package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Pattern;
import com.nghiabui.bootstrap.core.RelationCache;
import com.nghiabui.bootstrap.core.Word;
import com.nghiabui.common.AppException;

import java.util.HashSet;

public class ClearLogsUsecase implements Id_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public ClearLogsUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId) {
		try {
			gateway.saveRepoContent(repoId, new HashSet<>());

			for (int taskId : gateway.loadTaskIdsOfRepo(repoId)) {
				final RelationCache<Pattern, Word> patternCache = gateway.loadPatternCache(taskId);
				patternCache.clearLogs();
				if (patternCache.isDirty()) {
					gateway.savePatternCache(taskId, patternCache);
				}

				final RelationCache<Word, Pattern> wordCache = gateway.loadWordCache(taskId);
				wordCache.clearLogs();
				if (wordCache.isDirty()) {
					gateway.saveWordCache(taskId, wordCache);
				}
			}

			out.success();
		} catch (AppException e) {
			out.error(e.getMessage());
		}
	}

}
