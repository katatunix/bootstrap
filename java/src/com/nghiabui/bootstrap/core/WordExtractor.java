package com.nghiabui.bootstrap.core;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class WordExtractor implements SingleCombine<Pattern, Word> {

	@Override
	public Set<Word> apply(Pattern pattern, Log log) {
		final java.util.regex.Pattern javaPattern = pattern.getJavaPattern();
		final Matcher matcher = javaPattern.matcher(log.toString());
		final Set<Word> words = new HashSet<>();

		if (matcher.find()) {
			final int groupCount = matcher.groupCount();
			for (int i = 1; i <= groupCount; ++i) {
				final String w = matcher.group(i);
				if (!w.isEmpty()) {
					words.add(new Word(w));
				}
			}
		}

		return words;
	}

}
