package com.nghiabui.bootstrap.core;

public class ScoreRecord<T> {
	public final T element;
	public final double score;

	public ScoreRecord(T element, double score) {
		this.element = element;
		this.score = score;
	}
}
