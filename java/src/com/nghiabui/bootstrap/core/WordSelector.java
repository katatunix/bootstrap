package com.nghiabui.bootstrap.core;

import com.nghiabui.common.QSorter;
import com.nghiabui.common.SetOperation;

import java.util.*;
import java.util.function.Function;

public class WordSelector {

	private final Pool<Word> wordPool;
	private final Pool<Pattern> patternPool;
	private final Combine<Pattern, Word> combine;

	private final SetOperation<Word> setOperation = SetOperation.createFast();

	public WordSelector(Pool<Word> wordPool, Pool<Pattern> patternPool, Combine<Pattern, Word> combine) {
		this.wordPool = wordPool;
		this.patternPool = patternPool;
		this.combine = combine;
	}

	public List<ScoreRecord<Word>> selectBest(Collection<Word> words, int num, Collection<Pattern> refPatterns) {
		final Map<Pattern, Integer> frequencyCache = new HashMap<>();
		final Function<Pattern, Integer> frequencyProvider =
			pattern -> frequencyCache.computeIfAbsent(pattern, k -> frequencyOf(pattern));

		final List<ScoreRecord<Word>> result = new ArrayList<>();
		for (Word word : words) {
			result.add(new ScoreRecord<>(word, scoreWord(word, frequencyProvider, refPatterns)));
		}
		QSorter.sort(result, (rec1, rec2) -> rec1.score < rec2.score);
		if (num > result.size()) {
			num = result.size();
		}
		return result.subList(0, num);
	}

	public List<ScoreRecord<Word>> sort(Collection<Word> words) {
		return selectBest(words, words.size(), patternPool.seeds());
	}

	private int frequencyOf(Pattern pattern) {
		return setOperation.intersectionSize(combine.apply(pattern), wordPool.seeds());
	}

	private double scoreWord(Word word,
	                         Function<Pattern, Integer> frequencyProvider,
	                         Collection<Pattern> referredPatterns) {
		double sum = 0.0;
		int count = 0;
		for (Pattern pattern : referredPatterns) {
			final Set<Word> words = combine.apply(pattern);
			if (words.contains(word)) {
				++count;
				sum += MyMath.log2(1 + frequencyProvider.apply(pattern));
			}
		}
		return count == 0 ? 0.0 : sum / count;
	}

}
