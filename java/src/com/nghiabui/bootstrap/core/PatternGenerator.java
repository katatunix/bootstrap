package com.nghiabui.bootstrap.core;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PatternGenerator implements SingleCombine<Word, Pattern> {

	@Override
	public Set<Pattern> apply(Word word, Log log) {
		return gen(word.toString(), log.toString());
	}

	public static Set<Pattern> gen(String keyWord, String log) {
		if (keyWord.isEmpty()) return Collections.emptySet();

		final List<String> result = new ArrayList<>();
		int from = 0;
		while (true) {
			final int matchedIndex = log.indexOf(keyWord, from);
			if (matchedIndex == -1) break;
			result.addAll(genAtMatching(matchedIndex, keyWord, log));
			from = matchedIndex + 1;
		}
		return result.stream().map(Pattern::new).collect(Collectors.toSet());
	}

	private static final String PUNCTS = "\t()[]{}<>\\/*+-^$@#%=:,;!?";

	private static boolean isPunct(char ch) {
		return PUNCTS.indexOf(ch) != -1;
	}

	private static List<String> genAtMatching(int keyWordStart, String keyWord, String log) {
		final int keyWordEnd = keyWordStart + keyWord.length();
		if (!areValidWordBounds(log, keyWordStart - 1, keyWordEnd)) {
			return Collections.emptyList();
		}

		int tagIndex = findTagIndex(log, keyWordStart - 1);
		final int punctIndex = findLastIndex(
			log,
			(tagIndex == -1 ? keyWordStart : tagIndex) - 1,
			PatternGenerator::isPunct
		);
		if (tagIndex == -1) {
			tagIndex = keyWordStart;
		}

		final String prefix    = log.substring(0, punctIndex + 1);
		final String postPre   = log.substring(punctIndex + 1, tagIndex);
		final String tag       = log.substring(tagIndex, keyWordStart);
		final String suffix    = log.substring(keyWordEnd);

		final String prefixRegex        = genPrefix(prefix);
		final String postPreRegex       = genLettersAndDigitsOnly(postPre);
		final String tagRegex           = genLiteral(tag);
		final String wordRegex          = genLettersAndDigitsOnly(keyWord);
		final String suffixRegex        = genSuffix(suffix);

		return Collections.singletonList(
			"^" + prefixRegex + postPreRegex + tagRegex + "(" + wordRegex + ")" + suffixRegex
		);
	}

	private static String genPrefix(String prefix) {
		if (prefix.isEmpty()) return "";
		final char punct = prefix.charAt(prefix.length() - 1);
		final int count = countChar(punct, prefix);
		final String lit = genLiteral(punct);
		return "(?:[^" + lit + "]*" + lit + "){" + count + "}";
	}

	private static String genSuffix(String suffix) {
		return suffix.isEmpty() ? "$" : genLiteral(suffix.charAt(0));
	}

	//===========================================================================================================

	public static int countChar(char ch, String str) {
		int count = 0;
		for (int i = 0; i < str.length(); ++i)
			if (str.charAt(i) == ch) ++count;
		return count;
	}

	public static int findLastIndex(String str, int from, Predicate<Character> condition) {
		for (int i = from; i >= 0; --i) {
			if (condition.test(str.charAt(i))) return i;
		}
		return -1;
	}

	public static boolean isLetter(char ch) {
		return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_';
	}

	public static boolean isDigit(char ch) {
		return '0' <= ch && ch <= '9';
	}

	public static boolean isLetterOrDigit(char ch) {
		return isLetter(ch) || isDigit(ch);
	}

	public static boolean isSpace(char ch) {
		return ch == ' ' || ch == '\t';
	}

	public static String genLiteral(char ch) {
		return genLiteral("" + ch);
	}

	public static String genLiteral(String str) {
		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < str.length(); ++i) {
			final char ch = str.charAt(i);
			if (escaped(ch)) {
				sb.append("\\");
			}
			sb.append(ch);
		}
		return sb.toString();
	}

	// TODO
	// http://stackoverflow.com/questions/14134558/list-of-all-special-characters-that-need-to-be-escaped-in-a-regex
	private static final String ESC = "\\.[]{}()*+-?^$|";

	public static boolean escaped(char ch) {
		return ESC.indexOf(ch) != -1;
	}

	public static boolean areValidWordBounds(String log, int left, int right) {
		return (left < 0 || !isLetterOrDigit(log.charAt(left))) &&
			(right >= log.length() || !isLetterOrDigit(log.charAt(right)));
	}

	private enum S {
		NONE,
		LITERAL,
		LETTER,
		DIGIT
	}

	public static String genLettersAndDigitsOnly(String word) {
		final StringBuilder result = new StringBuilder();
		S state = S.NONE;
		for (int i = 0; i < word.length(); ++i) {
			char ch = word.charAt(i);
			if (isDigit(ch)) {
				if (state != S.DIGIT) {
					result.append("\\d+");
				}
				state = S.DIGIT;
			} else if (isLetter(ch)) {
				if (state != S.LETTER) {
					result.append("\\w+");
				}
				state = S.LETTER;
			} else {
				result.append(genLiteral(ch));
				state = S.LITERAL;
			}
		}
		return result.toString();
	}

	public static int findTagIndex(String log, int from) {
		int i = findLastIndex(log, from, ch -> !isSpace(ch));
		if (i == -1 || !isTagDelim(log.charAt(i))) return -1;

		i = findLastIndex(log, i - 1, ch -> !isSpace(ch));
		if (i == -1 || !isLetterOrDigit(log.charAt(i))) return -1;

		i = findLastIndex(log, i - 1, ch -> !isLetterOrDigit(ch));

		++i;
		return isLetter(log.charAt(i)) ? i : -1;
	}

	private static boolean isTagDelim(char ch) {
		return ch == '=' || ch == ':';
	}

}
