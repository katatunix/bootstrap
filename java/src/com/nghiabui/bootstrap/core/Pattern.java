package com.nghiabui.bootstrap.core;

import java.io.Serializable;

public class Pattern implements Serializable {

	private final String value;

	private java.util.regex.Pattern javaPattern = null;

	public Pattern(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	public java.util.regex.Pattern getJavaPattern() {
		synchronized (this) {
			if (javaPattern == null) {
				javaPattern = java.util.regex.Pattern.compile(value);
			}
		}
		return javaPattern;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Pattern pattern = (Pattern) o;

		return value.equals(pattern.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

}
