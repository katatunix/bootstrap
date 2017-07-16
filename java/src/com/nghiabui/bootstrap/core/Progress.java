package com.nghiabui.bootstrap.core;

import java.util.Collection;
import java.util.List;

public interface Progress {

	void beginLearning(int numWords, int numPatterns);
	void beginIter(int iteration);

	void beginGenNewPatterns(int numWords);
	void endGenNewPatterns(Collection<Pattern> patterns, int numWordsRemain);

	void beginSelectBestPatterns(int numPatterns);
	void endSelectBestPatterns(BestPatterns best);

	void beginExtractNewWords();
	void endExtractNewWords(Collection<Word> words);

	void beginSelectBestWords();
	void endSelectBestWords(List<ScoreRecord<Word>> best);

	void endBootstrap(StopReason reason, Collection<Word> newWords, Collection<Pattern> newPatterns);
	
	void beginPrunning(int numNewPatterns);
	void endPruning(BestPatterns best);
	
}
