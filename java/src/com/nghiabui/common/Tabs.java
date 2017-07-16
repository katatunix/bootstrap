package com.nghiabui.common;

public class Tabs {
	
	private static final int MAX_TABS = 16;
	public static String[] tabs = new String[MAX_TABS + 1];
	
	static {
		tabs[0] = "";
		for (int i = 1; i <= MAX_TABS; ++i) {
			tabs[i] = tabs[i - 1] + '\t';
		}
	}
	
}
