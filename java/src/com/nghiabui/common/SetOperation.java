package com.nghiabui.common;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.*;

public class SetOperation<T> {
	
	public static <T> boolean areEqual(Set<T> lhs, Set<T> rhs) {
		return lhs.size() == rhs.size() && lhs.containsAll(rhs) && rhs.containsAll(lhs);
	}
	
	public static <T> Set<T> op2set(Optional<T> op) {
		if (op.isPresent()) {
			final Set<T> result = new HashSet<>();
			result.add(op.get());
			return result;
		}
		return Collections.emptySet();
	}
	
	public static <T> SetOperation<T> createFast() {
		return new SetOperation<>(HashSet::new);
	}
	
	public static <T> SetOperation<T> createOrdered() {
		return new SetOperation<>(LinkedHashSet::new);
	}
	
	private final Supplier<Set<T>> setCreator;
	
	public SetOperation(Supplier<Set<T>> setCreator) {
		this.setCreator = setCreator;
	}
	
	public Set<T> newSet(Collection<T> coll) {
		final Set<T> result = setCreator.get();
		result.addAll(coll);
		return result;
	}
	
	public Set<T> newSet(T... es) {
		final Set<T> result = setCreator.get();
		for (T e : es) {
			result.add(e);
		}
		return result;
	}
	
	public Set<T> intersection(Set<T> left, Set<T> right) {
		return left.stream()
			.filter(right::contains)
			.collect(collector());
	}
	
	private Collector<T, ?, Set<T>> collector() {
		return Collectors.toCollection(setCreator);
	}
	
	public Set<T> intersection(Collection<Set<T>> list) {
		final Optional<Set<T>> result = list.stream().reduce(this::intersection);
		return result.isPresent() ? result.get() : Collections.emptySet();
	}
	
	public int intersectionSize(Set<T> left, Set<T> right) {
		int count = 0;
		for (T a : left) {
			if (right.contains(a)) ++count;
		}
		return count;
	}
	
	public Set<T> subtract(Set<T> left, Set<T> right) {
		return left.stream()
			.filter(item -> !right.contains(item))
			.collect(collector());
	}
	
	@SafeVarargs
	public final Set<T> subtract(Set<T> left, Set<T>... rights) {
		Set<T> result = left;
		for (Set<T> right : rights) {
			result = subtract(result, right);
		}
		return result;
	}
	
	public Set<T> union(Collection<Set<T>> sets) {
		final Set<T> result = setCreator.get();
		for (Set<T> set : sets) {
			result.addAll(set);
		}
		return result;
	}
	
	@SafeVarargs
	public final Set<T> union(Set<T>... sets) {
		final Set<T> result = setCreator.get();
		for (Set<T> set : sets) {
			result.addAll(set);
		}
		return result;
	}
	
	public boolean isSub(Set<T> set1, Set<T> set2) {
		return subtract(set1, set2).isEmpty();
	}
	
	@SafeVarargs
	public final boolean found(T e, Set<T>... sets) {
		for (Set<T> set : sets) {
			if (set.contains(e)) return true;
		}
		return false;
	}
	
	@SafeVarargs
	public final boolean hasSpecialElement(Set<T> set, Set<T>... sets) {
		for (T e : set) {
			if (!found(e, sets)) return true;
		}
		return false;
	}
	
}
