package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.common.AppException;
import com.nghiabui.common.Tuple;
import com.nghiabui.common.Tuple3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class ViewTaskUsecase {

	private final Gateway gateway;
	private final ViewTaskOut out;

	public ViewTaskUsecase(Gateway gateway, ViewTaskOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	public void execute(int taskId, boolean isFull, boolean viewsLogs) {
		final String taskName;
		final Pool<Pattern> patternPool;
		final Pool<Word> wordPool;
		final Param param;
		final int repoId;
		final String repoName;
		try {
			taskName    = gateway.loadTaskName(taskId);
			patternPool = gateway.loadPatternPool(taskId);
			wordPool    = gateway.loadWordPool(taskId);
			param       = gateway.loadParam(taskId);
			repoId      = gateway.loadRepoIdOfTask(taskId);
			repoName    = gateway.loadRepoName(repoId);
		} catch (AppException e) {
			out.viewTaskError(e.getMessage());
			return;
		}

		out.viewTaskSuccess(taskId, taskName, repoId, repoName, patternPool, wordPool, param);

		if (!isFull) return;

		//=================================================================================================
		final Set<Log> logs;
		final RelationCache<Pattern, Word> patternCache;
		try {
			out.loadingRepo(repoId);
			logs = gateway.loadRepoContent(repoId);
			out.loadingPatternCache();
			patternCache = gateway.loadPatternCache(taskId);
		} catch (AppException e) {
			out.viewTaskError(e.getMessage());
			return;
		}
		patternCache.retain(patternPool.seeds());
		final Combine<Pattern, Word> extractWords = new Combine<>(
			new CachedMultiLogCombine<>(
				new ParallelMultiLogCombine<>(logs, new WordExtractor()),
				patternCache
			)
		);

		final Optional<Set<Log>> NONE = Optional.empty();

		//=================================================================================================
		out.beginViewRelation_Pattern_Word();
		final PatternSelector patternSelector = new PatternSelector(wordPool, patternPool, extractWords);
		for (ScoreRecord<Pattern> patternRecord : patternSelector.sort(patternPool.seeds())) {
			final FriendMap<Word> fm = patternCache.get(patternRecord.element);
			final Collection<Tuple3<Word, WordStatus, Optional<Set<Log>>>> words = new ArrayList<>();
			int numSeeds = 0, numForbiddens = 0, numOthers = 0;

			for (Word word : fm.keys()) {
				final WordStatus status = makeWordStatus(word, wordPool);
				if (status == WordStatus.SEED) ++numSeeds;
				else if (status == WordStatus.FORBIDDEN) ++ numForbiddens;
				else ++numOthers;

				final Optional<Set<Log>> op = viewsLogs ? Optional.of(fm.getLogs(word)) : NONE;

				words.add(new Tuple3<>(word, status, op));
			}
			out.viewPatternRelation(patternRecord, numSeeds, numForbiddens, numOthers, words);
		}

		//=================================================================================================
		out.beginViewRelation_Word_Pattern();
		final WordSelector wordSelector = new WordSelector(wordPool, patternPool, extractWords);
		for (ScoreRecord<Word> wordRecord : wordSelector.sort(wordPool.seeds())) {
			final Word word = wordRecord.element;
			final Collection<Tuple<Pattern, Optional<Set<Log>>>> patterns = new ArrayList<>();

			for (Pattern pattern : patternPool.seeds()) {
				final FriendMap<Word> fm = patternCache.get(pattern);
				if (fm.keys().contains(word)) {
					final Optional<Set<Log>> op = viewsLogs ? Optional.of(fm.getLogs(word)) : NONE;
					patterns.add(new Tuple<>(pattern, op));
				}
			}
			out.viewWordRelation(wordRecord, patterns);
		}

		//=================================================================================================
		if (patternCache.isDirty()) {
			out.savingPatternCache();
			try {
				gateway.savePatternCache(taskId, patternCache);
			} catch (AppException e) {
				out.viewTaskError(e.getMessage());
			}
		}
	}

	private WordStatus makeWordStatus(Word word, Pool<Word> wordPool) {
		if (wordPool.isSeed(word)) return WordStatus.SEED;
		if (wordPool.isForbidden(word)) return WordStatus.FORBIDDEN;
		return WordStatus.OTHER;
	}

}
