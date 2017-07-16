package com.nghiabui.bootstrap.core;

import java.util.List;

public class BestPatterns {
	public final List<ScoreRecord<Pattern>> records;
	public final double overallScore;

	public BestPatterns(List<ScoreRecord<Pattern>> records, double overallScore) {
		this.records = records;
		this.overallScore = overallScore;
	}
}
