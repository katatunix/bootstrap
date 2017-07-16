package com.nghiabui.bootstrap.core;

import java.io.Serializable;

public class Word implements Serializable {

	private final String value;

	public Word(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Word word = (Word) o;

		return value.equals(word.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

}
