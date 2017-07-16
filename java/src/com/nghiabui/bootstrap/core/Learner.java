package com.nghiabui.bootstrap.core;

import com.nghiabui.common.Tuple;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Learner {

	public static void learn(Pool<Word> wordPool,
	                         Pool<Pattern> patternPool,
	                         Param param,
	                         Combine<Pattern, Word> extractWords,
	                         Combine<Word, Pattern> genPatterns,
	                         Progress progress) {
		progress.beginLearning(wordPool.seeds().size(), patternPool.seeds().size());

		final Set<Pattern> allPatterns = new HashSet<>(patternPool.seeds());
		final Set<Pattern> allNewPatterns = new HashSet<>();
		final Set<Word> allNewWords = new HashSet<>();
		Set<Word> wordsForGen = wordPool.seeds();
		int count = 1;
		StopReason reason;

		final PatternSelector patternSelector = new PatternSelector(wordPool, patternPool, extractWords);
		final WordSelector wordSelector = new WordSelector(wordPool, patternPool, extractWords);

		while (true) {
			progress.beginIter(count);

			//=====================================================================================================
			progress.beginGenNewPatterns(wordsForGen.size());
			final Tuple<Set<Pattern>, Set<Word>> tup = genPatterns.apply(wordsForGen, param.timeoutSec);
			final Set<Pattern> newPatterns = new HashSet<>();
			for (Pattern pattern : tup.x) {
				if (patternPool.isNewAndValid(pattern) && !allPatterns.contains(pattern)) {
					newPatterns.add(pattern);
				}
			}
			allPatterns.addAll(newPatterns);
			allNewPatterns.addAll(newPatterns);
			final Set<Word> pendingWords = tup.y;
			progress.endGenNewPatterns(newPatterns, pendingWords.size());

			//=====================================================================================================
			progress.beginSelectBestPatterns(allPatterns.size());
			final BestPatterns best = patternSelector.selectBest(allNewPatterns, param.threshold);
			progress.endSelectBestPatterns(best);
			final Set<Pattern> bestPatterns = new HashSet<>();
			for (ScoreRecord<Pattern> record : best.records) {
				bestPatterns.add(record.element);
			}

			if (bestPatterns.isEmpty() && pendingWords.isEmpty()) {
				reason = StopReason.NO_MORE_BEST_PATTERNS;
				break;
			}

			//=====================================================================================================
			progress.beginExtractNewWords();
			final Set<Word> newWords = new HashSet<>();
			for (Word word : extractWords.apply(bestPatterns)) {
				if (wordPool.isNewAndValid(word)) {
					newWords.add(word);
				}
			}
			progress.endExtractNewWords(newWords);

			//=====================================================================================================
			progress.beginSelectBestWords();
			final List<ScoreRecord<Word>> bw = wordSelector.selectBest(newWords, param.bestWordNumber, allPatterns);
			for (ScoreRecord<Word> record : bw) {
				final Word word = record.element;
				wordPool.addSeed(word);
				allNewWords.add(word);
				pendingWords.add(word);
			}
			wordsForGen = pendingWords;
			progress.endSelectBestWords(bw);

			if (count == param.iter) {
				reason = StopReason.ENOUGH_ITERATIONS;
				break;
			}
			++count;
		}

		progress.endBootstrap(reason, allNewWords, allNewPatterns);

		//======================================================================================================
		progress.beginPrunning(allNewPatterns.size());
		final BestPatterns finalPatterns = patternSelector.prune(allNewPatterns, param.threshold);
		for (ScoreRecord<Pattern> record : finalPatterns.records) {
			patternPool.addSeed(record.element);
		}
		progress.endPruning(finalPatterns);
	}

}
