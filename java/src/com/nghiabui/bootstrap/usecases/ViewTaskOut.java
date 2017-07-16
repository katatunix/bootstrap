package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.common.Tuple;
import com.nghiabui.common.Tuple3;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ViewTaskOut {

	void viewTaskSuccess(int taskId,
	                     String taskName,
	                     int repoId,
	                     String repoName,
	                     Pool<Pattern> patternPool,
	                     Pool<Word> wordPool,
	                     Param param);
	void viewTaskError(String message);

	void beginViewRelation_Pattern_Word();
	void viewPatternRelation(ScoreRecord<Pattern> patternRecord,
	                         int numSeeds, int numForbiddens, int numOthers,
	                         Collection<Tuple3<Word, WordStatus, Optional<Set<Log>>>> words);

	void beginViewRelation_Word_Pattern();
	void viewWordRelation(ScoreRecord<Word> wordRecord, Collection<Tuple<Pattern, Optional<Set<Log>>>> patterns);

	void loadingRepo(int repoId);
	void loadingPatternCache();
	void savingPatternCache();

}
