package com.nghiabui.bootstrap.core;

import com.nghiabui.common.QSorter;
import com.nghiabui.common.SetOperation;

import java.util.*;
import java.util.function.Function;

public class PatternSelector {

	private final Pool<Word> wordPool;
	private final Pool<Pattern> patternPool;
	private final Combine<Pattern, Word> combine;

	private final SetOperation<Word> setOperation = SetOperation.createFast();

	public PatternSelector(Pool<Word> wordPool, Pool<Pattern> patternPool, Combine<Pattern, Word> combine) {
		this.wordPool = wordPool;
		this.patternPool = patternPool;
		this.combine = combine;
	}

	private double scoreWordSet(Set<Word> words) {
		final int frequency = setOperation.intersectionSize(wordPool.seeds(), words);
		if (frequency == 0) {
			return 0.0;
		}
		final double reliability = (double) frequency / (double) words.size();
		return reliability * MyMath.log2(frequency);
	}

	private double score(Pattern pattern) {
		return scoreWordSet(combine.apply(pattern));
	}

	public BestPatterns selectBest(Collection<Pattern> newPatterns, double threshold) {
		return abstractSelect(newPatterns, threshold, scores -> {
			int i = 0;
			while (i < scores.size() && !isSignificant(scores.get(i).element)) {
				++i;
			}
			return i + 1;
		});
	}

	public BestPatterns prune(Collection<Pattern> newPatterns, double threshold) {
		final int oldLen = patternPool.seeds().size();
		final BestPatterns best = abstractSelect(newPatterns, threshold, scores -> oldLen);
		final int bestLen = best.records.size();
		if (bestLen == 0) {
			return best;
		}
		return new BestPatterns(
			best.records.subList(oldLen, bestLen),
			best.overallScore
		);
	}

	private BestPatterns abstractSelect(Collection<Pattern> newPatterns,
	                                    double threshold,
	                                    Function< List<ScoreRecord<Pattern>>, Integer > minLenFun) {
		final List<ScoreRecord<Pattern>> scores = sort(patternPool.seeds());
		for (ScoreRecord<Pattern> record : sort(newPatterns)) {
			if (record.score >= threshold) {
				scores.add(record);
			}
		}

		final int minLen = minLenFun.apply(scores);

		final int len = scores.size();
		if (minLen > len) {
			return new BestPatterns(Collections.emptyList(), 0.0);
		}

		final int fromIndex = Math.max(minLen - 1, 0);

		final Set<Word> words = new HashSet<>();
		for (int i = 0; i < fromIndex; ++i) {
			final Pattern pattern = scores.get(i).element;
			words.addAll(combine.apply(pattern));
		}

		int index = -1;
		double overallScore = 0;
		for (int i = fromIndex; i < len; ++i) {
			final Pattern pattern = scores.get(i).element;
			words.addAll(combine.apply(pattern));

			final double blockScore = scoreWordSet(words);
			if (blockScore < threshold) continue;
			if (index == -1 || blockScore - overallScore >= 0.01) {
				index = i;
				overallScore = blockScore;
			}
		}
		if (index == -1) {
			return new BestPatterns(Collections.emptyList(), 0.0);
		}
		return new BestPatterns(scores.subList(0, index + 1), overallScore);
	}

	public List<ScoreRecord<Pattern>> sort(Collection<Pattern> patterns) {
		final List<ScoreRecord<Pattern>> records = new ArrayList<>();
		for (Pattern pattern : patterns) {
			records.add(new ScoreRecord<>(pattern, score(pattern)));
		}
		QSorter.sort(records, (rec1, rec2) -> rec1.score < rec2.score);
		return records;
	}

	private boolean isSignificant(Pattern pattern) {
		final Set<Word> extractedWords = combine.apply(pattern);
		return setOperation.hasSpecialElement(extractedWords, wordPool.seeds(), wordPool.forbiddens());
	}

}
