package com.nghiabui.bootstrap.core;

import com.nghiabui.common.SetOperation;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class LearnerTest {

	private final SetOperation<Pattern> patternSetOpe = SetOperation.createFast();
	private final SetOperation<Word> wordSetOpe = SetOperation.createFast();

	@Test
	public void example1() {
		final List<String> rawLogs = Arrays.asList(
			"Mar 10 16:49:29 mcdavid su(pam_unix)[9596]: session opened for user root by (uid=500)",
			"Mar 10 16:50:01 mcdavid crond(pam_unix)[9638]: session opened for user root by (uid=0)",
			"Mar 10 16:50:01 mcdavid hello(pam_unix)[9638]: session opened for user root by (uid=0)",
			"Mar 10 16:56:32 mcdavid ntpd[2544]: synchronized to 138.23.180.126, stratum 2"
		);
		final Set<Log> logs = new HashSet<>();
		for (String rawLog : rawLogs) {
			logs.add(new Log(rawLog));
		}

		final Pool<Word> wordPool = new Pool<>();
		final List<String> rawWords = Arrays.asList("crond", "hello");
		for (String rawWord : rawWords) {
			wordPool.addSeed(new Word(rawWord));
		}

		final Pool<Pattern> patternPool = new Pool<>();

		final Combine<Pattern, Word> extractWords = new Combine<>(
			new CachedMultiLogCombine<>(
				new SeqMultiLogCombine<>(logs, new WordExtractor()),
				new RelationCache<>()
			)
		);

		final Combine<Word, Pattern> genPatterns = new Combine<>(
			new CachedMultiLogCombine<>(
				new SeqMultiLogCombine<>(logs, new PatternGenerator()),
				new RelationCache<>()
			)
		);

		final Param param = new Param(10, 0.0, 5, 0);

		final Progress progress = new DummyProgress();

		Learner.learn(wordPool, patternPool, param, extractWords, genPatterns, progress);

		assertTrue(SetOperation.areEqual(
			patternPool.seeds(),
			patternSetOpe.newSet(new Pattern("^(?:[^:]*:){2}\\d+ \\w+ (\\w+)\\("))
		));

		assertTrue(SetOperation.areEqual(
			wordPool.seeds(),
			wordSetOpe.newSet(
				new Word("su"),
				new Word("crond"),
				new Word("hello")
			)
		));
	}

	@Test
	public void example2() {
		final List<String> rawLogs = Arrays.asList(
			"Mar 10 16,49,29 mcdavid su(pam_unix)[9596]: session opened for user root by (uid=500)",
			"Mar 10 16,50,01 mcdavid crond(pam_unix)[9638]: session opened for user root by (uid=0)",
			"Mar 10 16,50,01 mcdavid hello(pam_unix)[9638]: session opened for user root by (uid=0)",
			"Mar 10 16,56,32 mcdavid ntpd(2544): synchronized to 138.23.180.126, stratum 2",
			"Mar 10 16,56,32 mcdavid su[2544]: synchronized to 138.23.180.126, stratum 2",
			"Mar 10 16,56,32 mcdavid crond[2544]: synchronized to 138.23.180.126, stratum 2"
		);
		final Set<Log> logs = new HashSet<>();
		for (String rawLog : rawLogs) {
			logs.add(new Log(rawLog));
		}

		final Pool<Word> wordPool = new Pool<>();
		final List<String> rawWords = Arrays.asList("su", "crond", "hello");
		for (String rawWord : rawWords) {
			wordPool.addSeed(new Word(rawWord));
		}

		final Pool<Pattern> patternPool = new Pool<>();

		final Combine<Pattern, Word> extractWords = new Combine<>(
			new CachedMultiLogCombine<>(
				new SeqMultiLogCombine<>(logs, new WordExtractor()),
				new RelationCache<>()
			)
		);

		final Combine<Word, Pattern> genPatterns = new Combine<>(
			new CachedMultiLogCombine<>(
				new ParallelMultiLogCombine<>(logs, new PatternGenerator()),
				new RelationCache<>()
			)
		);

		final Param param = new Param(10, 0.7, 5, 60);

		final Progress progress = new DummyProgress();

		Learner.learn(wordPool, patternPool, param, extractWords, genPatterns, progress);

		assertTrue(SetOperation.areEqual(
			patternPool.seeds(),
			patternSetOpe.newSet(new Pattern("^(?:[^,]*,){2}\\d+ \\w+ (\\w+)\\("))
		));

		assertTrue(SetOperation.areEqual(
			wordPool.seeds(),
			wordSetOpe.newSet(
				new Word("su"),
				new Word("crond"),
				new Word("hello"),
				new Word("ntpd")
			)
		));
	}

}
