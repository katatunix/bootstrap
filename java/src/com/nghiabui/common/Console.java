package com.nghiabui.common;

import static com.nghiabui.common.Tabs.*;

public class Console {
	
	public void info(String str) {
		java.lang.System.out.print(str);
	}

	public void info(int tabNum, String str) {
		java.lang.System.out.print(tabs[tabNum] + str);
	}

	public void infoln(String line) {
		infoln(0, line);
	}
	
	public void infoln(int tabNum, String line) {
		java.lang.System.out.println(tabs[tabNum] + line);
	}
	
	public void infoln() {
		java.lang.System.out.println();
	}
	
	public void warnln(String line) {
		warnln(0, line);
	}
	
	public void warnln(int tabNum, String line) {
		java.lang.System.out.println(tabs[tabNum] + "[Warning] " + line);
	}
	
	public void errorln(String line) {
		errorln(0, line);
	}
	
	public void errorln(int tabNum, String line) {
		java.lang.System.err.println(tabs[tabNum] + "[Error] " + line);
	}
	
}
