package com.nghiabui.bootstrap.presentation.presenters;

public interface Console {

	Console info(String str);
	Console info(int tabNum, String str);
	
	Console infoln();
	Console infoln(String line);
	Console infoln(int tabNum, String line);
	
	Console errorln(String line);
	
}
