package com.nghiabui.common;

import java.lang.System;

public class Ticker {

	private long last;
	private final long begin;
	
	public Ticker() {
		begin = tick();
		last = begin;
	}

	public float elapse() {
		long newTick = tick();
		float sec = (newTick - last) / 1000.f;
		last = newTick;
		return sec;
	}
	
	public float duration() {
		return (tick() - begin) / 1000.f;
	}

	private static long tick() {
		return System.currentTimeMillis();
	}

}
