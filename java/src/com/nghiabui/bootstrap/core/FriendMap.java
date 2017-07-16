package com.nghiabui.bootstrap.core;

import java.io.Serializable;
import java.util.*;

public class FriendMap<Fri> implements Serializable {

	private final Map<Fri, Set<Log>> data = new HashMap<>();

	public void add(Fri friend, Log log) {
		final Set<Log> logs = data.computeIfAbsent(friend, k -> new HashSet<>());
		logs.add(log);
	}

	public void merge(FriendMap<Fri> other) {
		other.data.forEach((friend, hisLogs) -> {
			final Set<Log> myLogs = data.computeIfAbsent(friend, k -> new HashSet<>());
			myLogs.addAll(hisLogs);
		});
	}

	public void removeLogs(Collection<Log> logs) {
		final List<Fri> removed = new ArrayList<>(data.size());

		for (Map.Entry<Fri, Set<Log>> entry : data.entrySet()) {
			final Fri friend = entry.getKey();
			final Set<Log> friendLogs = entry.getValue();
			friendLogs.removeAll(logs);
			if (friendLogs.isEmpty()) {
				removed.add(friend);
			}
		}

		for (Fri friend : removed) {
			data.remove(friend);
		}
	}

	public Set<Fri> keys() {
		return data.keySet();
	}

	public Set<Log> getLogs(Fri friend) {
		return data.get(friend);
	}

	public void clear() {
		data.clear();
	}

	public int count() {
		return data.size();
	}

}
