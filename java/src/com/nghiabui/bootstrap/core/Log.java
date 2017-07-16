package com.nghiabui.bootstrap.core;

import java.io.Serializable;

public class Log implements Serializable {

	private final String value;

	public Log(String value) {
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

		Log log = (Log) o;

		return value.equals(log.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
