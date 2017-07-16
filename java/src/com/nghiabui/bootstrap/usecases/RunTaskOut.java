package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.*;

import java.util.Collection;
import java.util.List;

public interface RunTaskOut {

	void beginLearning(int numWords, int numPatterns);
	void beginIter(int iteration);

	void beginGenNewPatterns(int numWords);
	void endGenNewPatterns(Collection<Pattern> newPatterns, int numWordsRemain, float elapse);

	void beginSelectBestPatterns(int numPatterns);
	void endSelectBestPatterns(BestPatterns best, float elapse);

	void beginExtractNewWords();
	void endExtractNewWords(Collection<Word> newWords, float elapse);

	void beginSelectBestWords();
	void endSelectBestWords(List<ScoreRecord<Word>> bestWords, float elapse);

	void endBootstrap(StopReason reason, Collection<Word> newWords, Collection<Pattern> newPatterns);

	void beginPrunning(int numNewPatterns);
	void endPruning(BestPatterns best, float elapse);

	void endLearning(float elapse);

	//============================================

	void loadingRepo(int id);
	void loadingTask(int id);
	
	void runTaskSuccess();
	void runTaskError(String message);
	
}
