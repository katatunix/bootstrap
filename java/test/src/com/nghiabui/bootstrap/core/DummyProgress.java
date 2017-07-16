package com.nghiabui.bootstrap.core;

import java.util.Collection;
import java.util.List;

public class DummyProgress implements Progress {
	@Override
	public void beginLearning(int numWords, int numPatterns) {

	}

	@Override
	public void beginIter(int iteration) {

	}

	@Override
	public void beginGenNewPatterns(int numWords) {

	}

	@Override
	public void endGenNewPatterns(Collection<Pattern> patterns, int numWordsRemain) {

	}

	@Override
	public void beginSelectBestPatterns(int numPatterns) {

	}

	@Override
	public void endSelectBestPatterns(BestPatterns best) {

	}

	@Override
	public void beginExtractNewWords() {

	}

	@Override
	public void endExtractNewWords(Collection<Word> words) {

	}

	@Override
	public void beginSelectBestWords() {

	}

	@Override
	public void endSelectBestWords(List<ScoreRecord<Word>> best) {

	}

	@Override
	public void endBootstrap(StopReason reason, Collection<Word> newWords, Collection<Pattern> newPatterns) {

	}

	@Override
	public void beginPrunning(int numNewPatterns) {

	}

	@Override
	public void endPruning(BestPatterns best) {

	}

}
