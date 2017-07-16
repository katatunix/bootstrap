package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.common.AppException;
import com.nghiabui.common.Ticker;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RunTaskUsecase implements Id_Usecase {

	private final Gateway gateway;
	private final RunTaskOut out;

	public RunTaskUsecase(Gateway gateway, RunTaskOut out) {
		this.gateway = gateway;
		this.out = out;
	}

	public void execute(int taskId) {
		final Set<Log>                      logs;
		final Pool<Pattern>                 patternPool;
		final Pool<Word>                    wordPool;
		final RelationCache<Pattern, Word>  patternCache;
		final RelationCache<Word, Pattern>  wordCache;
		final Param param;
		try {
			int repoId      = gateway.loadRepoIdOfTask(taskId);
			out.loadingRepo(repoId);
			logs            = gateway.loadRepoContent(repoId);
			out.loadingTask(taskId);
			patternPool     = gateway.loadPatternPool(taskId);
			wordPool        = gateway.loadWordPool(taskId);
			patternCache    = gateway.loadPatternCache(taskId);
			wordCache       = gateway.loadWordCache(taskId);
			param           = gateway.loadParam(taskId);
		} catch (AppException e) {
			out.runTaskError(e.getMessage());
			return;
		}

		final Combine<Pattern, Word> extractWords = new Combine<>(
			new CachedMultiLogCombine<>(
				new ParallelMultiLogCombine<>(logs, new WordExtractor()),
				patternCache
			)
		);
		final Combine<Word, Pattern> genPatterns = new Combine<>(
			new CachedMultiLogCombine<>(
				new ParallelMultiLogCombine<>(logs, new PatternGenerator()),
				wordCache
			)
		);

		final Ticker localTicker = new Ticker();
		final Progress progress = new Progress() {
			public void beginLearning(int numWords, int numPatterns) {
				out.beginLearning(numWords, numPatterns);
			}
			public void beginIter(int iteration) {
				out.beginIter(iteration);
			}
			public void beginGenNewPatterns(int numWords) {
				out.beginGenNewPatterns(numWords);
			}
			public void endGenNewPatterns(Collection<Pattern> patterns, int numWordsRemain) {
				out.endGenNewPatterns(patterns, numWordsRemain, localTicker.elapse());
			}
			public void beginSelectBestPatterns(int numPatterns) {
				out.beginSelectBestPatterns(numPatterns);
			}
			public void endSelectBestPatterns(BestPatterns best) {
				out.endSelectBestPatterns(best, localTicker.elapse());
			}
			public void beginExtractNewWords() {
				out.beginExtractNewWords();
			}
			public void endExtractNewWords(Collection<Word> words) {
				out.endExtractNewWords(words, localTicker.elapse());
			}
			public void beginSelectBestWords() {
				out.beginSelectBestWords();
			}
			public void endSelectBestWords(List<ScoreRecord<Word>> best) {
				out.endSelectBestWords(best, localTicker.elapse());
			}
			public void endBootstrap(StopReason reason, Collection<Word> newWords, Collection<Pattern> newPatterns) {
				out.endBootstrap(reason, newWords, newPatterns);
			}
			public void beginPrunning(int numNewPatterns) {
				out.beginPrunning(numNewPatterns);
			}
			public void endPruning(BestPatterns best) {
				out.endPruning(best, localTicker.elapse());
			}
		};

		final Ticker mainTicker = new Ticker();
		Learner.learn(wordPool, patternPool, param, extractWords, genPatterns, progress);
		patternCache.retain(patternPool.seeds());
		wordCache.retain(wordPool.seeds());
		out.endLearning(mainTicker.elapse());

		try {
			gateway.savePatternPool(taskId, patternPool);
			gateway.saveWordPool(taskId, wordPool);
			if (patternCache.isDirty()) {
				gateway.savePatternCache(taskId, patternCache);
			}
			if (wordCache.isDirty()) {
				gateway.saveWordCache(taskId, wordCache);
			}
		} catch (AppException e) {
			out.runTaskError(e.getMessage());
			return;
		}

		out.runTaskSuccess();
	}

}
