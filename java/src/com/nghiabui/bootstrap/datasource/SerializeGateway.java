package com.nghiabui.bootstrap.datasource;

import com.nghiabui.bootstrap.core.*;
import com.nghiabui.bootstrap.usecases.Gateway;
import com.nghiabui.common.AppException;
import com.nghiabui.common.io.File;
import com.nghiabui.common.io.Folder;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class SerializeGateway implements Gateway {

	private final Folder dataFolder;

	public SerializeGateway(Folder dataFolder) {
		this.dataFolder = dataFolder;
	}

	@Override
	public int newRepoId() {
		return newId(file -> isRepoName_FileName(file.name()));
	}

	private int newId(Predicate<File> filter) {
		return dataFolder.childFiles().stream()
			.filter(filter)
			.map(file -> Integer.valueOf(file.ext()))
			.max((id1, id2) -> id1 < id2 ? -1 : 1)
			.map(i -> i + 1).orElse(1);
	}

	@Override
	public void deleteRepo(int repoId) {
		dataFolder.deleteChildFile(repoName_FileName(repoId));
		dataFolder.deleteChildFile(repoContent_FileName(repoId));
	}

	@Override
	public String loadRepoName(int repoId) {
		return (String) loadObj(repoName_FileName(repoId));
	}

	@Override
	public void saveRepoName(int repoId, String name) {
		saveObj(repoName_FileName(repoId), name);
	}

	@Override
	public Set<Log> loadRepoContent(int repoId) {
		return (Set<Log>) loadObj(repoContent_FileName(repoId));
	}

	@Override
	public void saveRepoContent(int repoId, Set<Log> logs) {
		saveObj(repoContent_FileName(repoId), logs);
	}

	@Override
	public Map<Integer, String> loadAllRepoNames() {
		final Map<Integer, String> result = new HashMap<>();
		for (File file : dataFolder.childFiles()) {
			if (isRepoName_FileName(file.name())) {
				final int id = Integer.valueOf(file.ext());
				result.put(id, loadRepoName(id));
			}
		}
		return result;
	}

	@Override
	public Set<Integer> loadTaskIdsOfRepo(int repoId) {
		final Set<Integer> taskIds = new HashSet<>();
		for (File file : dataFolder.childFiles()) {
			if (isTaskName_FileName(file.name())) {
				final int taskId = Integer.valueOf(file.ext());
				if (loadRepoIdOfTask(taskId) == repoId) {
					taskIds.add(taskId);
				}
			}
		}
		return taskIds;
	}

	//============================================================================================

	@Override
	public int newTaskId() {
		return newId(file -> isTaskName_FileName(file.name()));
	}

	@Override
	public void deleteTask(int taskId) {
		dataFolder.deleteChildFile(taskName_FileName(taskId));
		dataFolder.deleteChildFile(patternPool_FileName(taskId));
		dataFolder.deleteChildFile(wordPool_FileName(taskId));
		dataFolder.deleteChildFile(patternCache_FileName(taskId));
		dataFolder.deleteChildFile(wordCache_FileName(taskId));
		dataFolder.deleteChildFile(param_FileName(taskId));
		dataFolder.deleteChildFile(taskFK_FileName(taskId));
	}

	@Override
	public int loadRepoIdOfTask(int taskId) {
		return (Integer) loadObj(taskFK_FileName(taskId));
	}

	@Override
	public void saveRepoIdOfTask(int taskId, int repoId) {
		saveObj(taskFK_FileName(taskId), repoId);
	}

	@Override
	public String loadTaskName(int taskId) {
		return (String) loadObj(taskName_FileName(taskId));
	}

	@Override
	public void saveTaskName(int taskId, String name) {
		saveObj(taskName_FileName(taskId), name);
	}

	@Override
	public Pool<Pattern> loadPatternPool(int taskId) {
		return (Pool<Pattern>) loadObj(patternPool_FileName(taskId));
	}

	@Override
	public void savePatternPool(int taskId, Pool<Pattern> pool) {
		saveObj(patternPool_FileName(taskId), pool);
	}

	@Override
	public Pool<Word> loadWordPool(int taskId) {
		return (Pool<Word>) loadObj(wordPool_FileName(taskId));
	}

	@Override
	public void saveWordPool(int taskId, Pool<Word> pool) {
		saveObj(wordPool_FileName(taskId), pool);
	}
	
	@Override
	public RelationCache<Pattern, Word> loadPatternCache(int taskId) {
		final RelationCache<Pattern, Word> cache =
			(RelationCache<Pattern, Word>) loadObj(patternCache_FileName(taskId));
		cache.resetDirty();
		return cache;
	}
	
	@Override
	public void savePatternCache(int taskId, RelationCache<Pattern, Word> cache) {
		saveObj(patternCache_FileName(taskId), cache);
	}

	@Override
	public RelationCache<Word, Pattern> loadWordCache(int taskId) {
		final RelationCache<Word, Pattern> cache =
			(RelationCache<Word, Pattern>) loadObj(wordCache_FileName(taskId));
		cache.resetDirty();
		return cache;
	}

	@Override
	public void saveWordCache(int taskId, RelationCache<Word, Pattern> cache) {
		saveObj(wordCache_FileName(taskId), cache);
	}

	@Override
	public Param loadParam(int taskId) {
		return (Param) loadObj(param_FileName(taskId));
	}

	@Override
	public void saveParam(int taskId, Param param) {
		saveObj(param_FileName(taskId), param);
	}
	
	//============================================================================================

	private Object loadObj(String fileName) {
		try (FileInputStream fileIn = new FileInputStream(dataFolder.findChildFile(fileName).path())) {
			try (ObjectInputStream in = new ObjectInputStream(fileIn)) {
				return in.readObject();
			}
		} catch (IOException | ClassNotFoundException e) {
			throw new AppException(e);
		}
	}

	private void saveObj(String fileName, Object obj) {
		try (FileOutputStream fileOut = new FileOutputStream(dataFolder.getChildFile(fileName).path())) {
			try (ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
				out.writeObject(obj);
			}
		} catch (IOException e) {
			throw new AppException(e);
		}
	}
	
	//============================================================================================

	private static final String REPO_NAME_PREFIX = "repoName.";
	private static String repoName_FileName(int id) {
		return REPO_NAME_PREFIX + id;
	}
	private static boolean isRepoName_FileName(String name) {
		return name.startsWith(REPO_NAME_PREFIX);
	}

	private static String repoContent_FileName(int id) {
		return "repoContent." + id;
	}
	
	//============================================================================================

	private static final String TASK_NAME_PREFIX = "taskName.";
	private static String taskName_FileName(int id) {
		return TASK_NAME_PREFIX + id;
	}
	private static boolean isTaskName_FileName(String name) {
		return name.startsWith(TASK_NAME_PREFIX);
	}

	private static String taskFK_FileName(int id) {
		return "taskFK." + id;
	}

	private static String patternPool_FileName(int id) {
		return "patternPool." + id;
	}

	private static String wordPool_FileName(int id) {
		return "wordPool." + id;
	}

	private static String patternCache_FileName(int id) {
		return "patternCache." + id;
	}

	private static String wordCache_FileName(int id) {
		return "wordCache." + id;
	}

	private static String param_FileName(int id) {
		return "param." + id;
	}

}
