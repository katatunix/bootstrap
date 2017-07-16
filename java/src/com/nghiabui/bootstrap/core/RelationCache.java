package com.nghiabui.bootstrap.core;

import java.io.Serializable;
import java.util.*;

public class RelationCache<Ele, Fri> implements Serializable {

	private final Map<Ele, FriendMap<Fri>> data = new HashMap<>();
	private boolean dirty = false;

	public void add(Ele ele, FriendMap<Fri> fm) {
		final FriendMap<Fri> curFm = data.get(ele);
		if (curFm != null) {
			if (fm.count() > 0) {
				curFm.merge(fm);
				dirty = true;
			}
		} else {
			data.put(ele, fm);
			dirty = true;
		}
	}

	public FriendMap<Fri> get(Ele ele) {
		return data.get(ele);
	}

	public Set<Ele> keys() {
		return data.keySet();
	}

	public void retain(Collection<Ele> elements) {
		final List<Ele> removed = new ArrayList<>();
		for (Ele e : data.keySet()) {
			if (!elements.contains(e)) {
				removed.add(e);
			}
		}
		for (Ele e : removed) {
			data.remove(e);
			dirty = true;
		}
	}

	public void removeLogs(Collection<Log> logs) {
		for (FriendMap<Fri> fm : data.values()) {
			fm.removeLogs(logs);
			dirty = true;
		}
	}

	public void clearLogs() {
		for (FriendMap<Fri> fm : data.values()) {
			fm.clear();
			dirty = true;
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	//=======
	public void resetDirty() {
		dirty = false;
	}

}
