package com.nghiabui.bootstrap.core;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Pool<T> implements Serializable {

	private final Set<T> seeds = new HashSet<>();
	private final Set<T> forbiddens = new HashSet<>();

	public Set<T> seeds() {
		return seeds;
	}

	public Set<T> forbiddens() {
		return forbiddens;
	}

	public boolean isSeed(T e) {
		return seeds.contains(e);
	}

	public boolean isForbidden(T e) {
		return forbiddens.contains(e);
	}

	public boolean isNewAndValid(T e) {
		return !isSeed(e) && !isForbidden(e);
	}

	public void addSeed(T e) {
		seeds.add(e);
		forbiddens.remove(e);
	}

	public void addSeeds(Collection<T> es) {
		seeds.addAll(es);
		forbiddens.removeAll(es);
	}

	public void addForbidden(T e) {
		seeds.remove(e);
		forbiddens.add(e);
	}

	public void addForbiddens(Collection<T> es) {
		seeds.removeAll(es);
		forbiddens.addAll(es);
	}

	public void remove(T e) {
		seeds.remove(e);
		forbiddens.remove(e);
	}

	public void remove(Collection<T> es) {
		seeds.removeAll(es);
		forbiddens.removeAll(es);
	}

	public void clear() {
		seeds.clear();
		forbiddens.clear();
	}

}
