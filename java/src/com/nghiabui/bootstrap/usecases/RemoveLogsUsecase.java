package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.Log;
import com.nghiabui.bootstrap.core.Pattern;
import com.nghiabui.bootstrap.core.RelationCache;
import com.nghiabui.bootstrap.core.Word;
import com.nghiabui.common.AppException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class RemoveLogsUsecase implements Id_Strings_Usecase {

	private final Gateway gateway;
	private final BasicOut out;

	public RemoveLogsUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId, Collection<String> rawRemovedLogs) {
		final Set<Log> removedLogs = new HashSet<>();
		for (String rawLog : rawRemovedLogs) {
			removedLogs.add(new Log(rawLog));
		}

		try {
			final Set<Log> logs = gateway.loadRepoContent(repoId);
			logs.removeAll(removedLogs);
			gateway.saveRepoContent(repoId, logs);

			for (int taskId : gateway.loadTaskIdsOfRepo(repoId)) {
				final RelationCache<Pattern, Word> patternCache = gateway.loadPatternCache(taskId);
				patternCache.removeLogs(removedLogs);
				if (patternCache.isDirty()) {
					gateway.savePatternCache(taskId, patternCache);
				}

				final RelationCache<Word, Pattern> wordCache = gateway.loadWordCache(taskId);
				wordCache.removeLogs(removedLogs);
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
