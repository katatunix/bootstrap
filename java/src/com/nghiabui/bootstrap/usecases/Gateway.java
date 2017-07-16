package com.nghiabui.bootstrap.usecases;

import com.nghiabui.bootstrap.core.*;

import java.util.Map;
import java.util.Set;

public interface Gateway {

	int newRepoId();
	void deleteRepo(int repoId);

	String loadRepoName(int repoId);
	void saveRepoName(int repoId, String name);

	Set<Log> loadRepoContent(int repoId);
	void saveRepoContent(int repoId, Set<Log> content);

	Map<Integer, String> loadAllRepoNames();
	Set<Integer> loadTaskIdsOfRepo(int repoId);

	//=====================================================================

	int newTaskId();
	void deleteTask(int taskId);

	int loadRepoIdOfTask(int taskId);
	void saveRepoIdOfTask(int taskId, int repoId);

	String loadTaskName(int taskId);
	void saveTaskName(int taskId, String name);

	Pool<Pattern> loadPatternPool(int taskId);
	void savePatternPool(int taskId, Pool<Pattern> pool);

	Pool<Word> loadWordPool(int taskId);
	void saveWordPool(int taskId, Pool<Word> pool);

	RelationCache<Pattern, Word> loadPatternCache(int taskId);
	void savePatternCache(int taskId, RelationCache<Pattern, Word> cache);

	RelationCache<Word, Pattern> loadWordCache(int taskId);
	void saveWordCache(int taskId, RelationCache<Word, Pattern> cache);

	Param loadParam(int taskId);
	void saveParam(int taskId, Param param);

}
