package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.common.AppException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class AddLogsUsecase implements Id_Strings_Usecase {
	private final Gateway gateway;
	private final BasicOut out;

	public AddLogsUsecase(Gateway gateway, BasicOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	@Override
	public void execute(int repoId, Collection<String> rawNewLogs) {
		final Set<Log> newLogs = new HashSet<>();
		for (String rawLog : rawNewLogs) {
			newLogs.add(new Log(rawLog));
		}

		try {
			final Set<Log> logs = gateway.loadRepoContent(repoId);
			logs.addAll(newLogs);
			gateway.saveRepoContent(repoId, logs);

			final MultiLogCombine<Pattern, Word> extractWords =
				new ParallelMultiLogCombine<>(newLogs, new WordExtractor());
			final MultiLogCombine<Word, Pattern> genPatterns =
				new ParallelMultiLogCombine<>(newLogs, new PatternGenerator());

			for (int taskId : gateway.loadTaskIdsOfRepo(repoId)) {
				final RelationCache<Pattern, Word> patternCache = gateway.loadPatternCache(taskId);
				for (Pattern pattern : patternCache.keys()) {
					patternCache.add(pattern, extractWords.apply(pattern));
				}
				if (patternCache.isDirty()) {
					gateway.savePatternCache(taskId, patternCache);
				}

				final RelationCache<Word, Pattern> wordCache = gateway.loadWordCache(taskId);
				for (Word word : wordCache.keys()) {
					wordCache.add(word, genPatterns.apply(word));
				}
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
