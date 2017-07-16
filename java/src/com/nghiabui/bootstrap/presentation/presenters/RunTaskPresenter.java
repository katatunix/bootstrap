package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.bootstrap.usecases.RunTaskOut;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RunTaskPresenter implements RunTaskOut {
	
	private final Console console;
	
	public RunTaskPresenter(Console console) {
		this.console = console;
	}
	
	@Override
	public void beginLearning(int numWords, int numPatterns) {
		console.infoln()
			.infoln(String.format("BOOTSTRAPPING WITH %d SEED WORDS AND %d SEED PATTERNS", numWords, numPatterns))
			.infoln();
	}
	
	private void hr() {
		console.infoln("===================================================================================");
	}
	
	@Override
	public void beginIter(int iteration) {
		hr();
		console.infoln("ITERATION: " + iteration);
		console.infoln();
	}
	
	@Override
	public void beginGenNewPatterns(int numWords) {
		console.info(String.format("Generating new patterns from %d words ... ", numWords));
	}
	
	@Override
	public void endGenNewPatterns(Collection<Pattern> newPatterns, int numWordsRemain, float elapse) {
		showTime(elapse);
		if (numWordsRemain > 0) {
			console.info(String.format("TIMEOUT!!! %d words left!!! ", numWordsRemain));
		}
		console.infoln(newPatterns.size() + " new patterns:");
		newPatterns.forEach(pattern -> console.infoln(1, pattern.toString()));
		console.infoln();
	}

	private void showTime(float elapse) {
		console.infoln("Time: " + elapse + "s");
	}
	
	@Override
	public void beginSelectBestPatterns(int numPatterns) {
		console.info(String.format("Selecting best patterns among %d patterns ... ", numPatterns));
	}
	
	@Override
	public void endSelectBestPatterns(BestPatterns best, float elapse) {
		printBestPatterns("", best, elapse);
	}

	private void printBestPatterns(String prefixLabel, BestPatterns best, float elapse) {
		showTime(elapse);
		final int len = best.records.size();
		console.info(prefixLabel + len + " best patterns");
		if (len > 0) {
			console.infoln(String.format(" with overall score [%.2f]:", best.overallScore));
		} else {
			console.infoln(":");
		}
		best.records.forEach(p -> console.infoln(1, String.format("%s [score=%.2f]", p.element, p.score)));
		console.infoln();
	}

	@Override
	public void beginExtractNewWords() {
		console.info("Extracting new words ... ");
	}
	
	@Override
	public void endExtractNewWords(Collection<Word> words, float elapse) {
		showTime(elapse);
		console.info(words.size() + " new words: ");
		Utils.printOneLine(console, words.stream().map(Object::toString).collect(Collectors.toList()));
		console.infoln();
	}
	
	@Override
	public void beginSelectBestWords() {
		console.info("Selecting best words ... ");
	}
	
	@Override
	public void endSelectBestWords(List<ScoreRecord<Word>> bestWords, float elapse) {
		showTime(elapse);
		console.info(bestWords.size() + " best words: ");
		final List<String> strings = bestWords.stream()
			.map(rec -> String.format("%s [score=%.2f]", rec.element, rec.score))
			.collect(Collectors.toList());
		Utils.printOneLine(console, strings);
		console.infoln();
	}
	
	@Override
	public void endBootstrap(StopReason reason, Collection<Word> newWords, Collection<Pattern> newPatterns) {
		hr();
		console.infoln("End bootstrapping because of " + reason.toString())
			.infoln();

		console.info(String.format("Learned %d new words: ", newWords.size()));
		Utils.printOneLine(console, newWords.stream().map(Object::toString).collect(Collectors.toList()));
		console.infoln();

		console.infoln(String.format("Collected %d new patterns:", newPatterns.size()));
		newPatterns.forEach(pattern -> console.infoln(1, pattern.toString()));
		console.infoln();
	}

	@Override
	public void beginPrunning(int numNewPatterns) {
		hr();
		console.info(String.format("Pruning with %d new patterns ... ", numNewPatterns));
	}

	@Override
	public void endPruning(BestPatterns best, float elapse) {
		printBestPatterns("Finally selected ", best, elapse);
	}

	@Override
	public void endLearning(float elapse) {
		console.infoln("LEARNING TIME: " + elapse + "s");
	}

	//===============================================================================================

	@Override
	public void loadingRepo(int id) {
		console.infoln("LOADING REPO [" + id + "]");
	}

	@Override
	public void loadingTask(int id) {
		console.infoln("LOADING TASK [" + id + "]");
	}

	@Override
	public void runTaskSuccess() {
		console.infoln("Successful");
	}
	
	@Override
	public void runTaskError(String message) {
		console.errorln(message);
	}

}
