package com.nghiabui.bootstrap.presentation.presenters;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.bootstrap.usecases.ViewTaskOut;
import com.nghiabui.bootstrap.usecases.WordStatus;
import com.nghiabui.common.Tuple;
import com.nghiabui.common.Tuple3;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ViewTaskPresenter implements ViewTaskOut {
	
	private final Console console;
	
	public ViewTaskPresenter(Console console) {
		this.console = console;
	}
	
	@Override
	public void viewTaskSuccess(int taskId,
	                            String taskName,
	                            int repoId,
	                            String repoName,
	                            Pool<Pattern> patternPool,
	                            Pool<Word> wordPool,
	                            Param param) {
		console.infoln("Task: [" + taskId + "] " + taskName);
		console.infoln("Repo: [" + repoId + "] " + repoName);
		console.infoln();
		
		console.infoln(patternPool.seeds().size() + " seed patterns:");
		patternPool.seeds().forEach(p -> console.infoln(1, p.toString()));
		console.infoln();

		console.infoln(patternPool.forbiddens().size() + " forbidden patterns:");
		patternPool.forbiddens().forEach(p -> console.infoln(1, p.toString()));
		console.infoln();
		
		console.info(wordPool.seeds().size() + " seed words: ");
		Utils.printOneLine(console, wordPool.seeds().stream().map(Object::toString).collect(Collectors.toList()));
		console.infoln();
		
		console.info(wordPool.forbiddens().size() + " forbidden words: ");
		Utils.printOneLine(console, wordPool.forbiddens().stream().map(Object::toString).collect(Collectors.toList()));
		console.infoln();
		
		console.infoln("Parameters:");
		console.infoln(1, "iter           : " + param.iter);
		console.infoln(1, "threshold      : " + param.threshold);
		console.infoln(1, "bestWordNumber : " + param.bestWordNumber);
		console.infoln(1, "timeoutSec     : " + param.timeoutSec);
	}
	
	@Override
	public void viewTaskError(String message) {
		console.errorln(message);
	}

	@Override
	public void beginViewRelation_Pattern_Word() {
		console.infoln();
		console.infoln("PATTERNS =======> WORDS:");
	}

	@Override
	public void viewPatternRelation(ScoreRecord<Pattern> patternRecord,
	                                int numSeeds, int numForbiddens, int numOthers,
	                                Collection<Tuple3<Word, WordStatus, Optional<Set<Log>>>> words) {
		console.infoln(String.format(
			"Pattern %s [score=%.2f] extracts %d words (%d seeds, %d forbiddens, and %d others):",
			patternRecord.element,
			patternRecord.score,
			words.size(), numSeeds, numForbiddens, numOthers
		));
		for (Tuple3<Word, WordStatus, Optional<Set<Log>>> t : words) {
			console.info(1, String.format("Word %s [%s]", t.x, t.y));
			printLogs(t.z);
		}
	}

	private void printLogs(Optional<Set<Log>> op) {
		if (op.isPresent()) {
			final Set<Log> logs = op.get();
			console.infoln(String.format(" at %d log events:", logs.size()));
			for (Log log : logs) {
				console.infoln(2, log.toString());
			}
		} else {
			console.infoln();
		}
	}

	@Override
	public void beginViewRelation_Word_Pattern() {
		console.infoln();
		console.infoln("WORDS =======> PATTERNS:");
	}

	@Override
	public void viewWordRelation(ScoreRecord<Word> wordRecord,
	                             Collection<Tuple<Pattern, Optional<Set<Log>>>> patterns) {
		console.infoln(String.format(
			"Word %s [score=%.2f] is extracted by %d patterns:",
			wordRecord.element,
			wordRecord.score,
			patterns.size()
		));
		for (Tuple<Pattern, Optional<Set<Log>>> t : patterns) {
			console.info(1, String.format("Pattern %s", t.x));
			printLogs(t.y);
		}
	}

	@Override
	public void loadingRepo(int repoId) {
		console.infoln();
		console.infoln(String.format("LOADING REPO [%d]", repoId));
	}

	@Override
	public void loadingPatternCache() {
		console.infoln("LOADING PATTERN CACHE");
	}

	@Override
	public void savingPatternCache() {
		console.infoln();
		console.infoln("SAVING PATTERN CACHE");
	}

}
