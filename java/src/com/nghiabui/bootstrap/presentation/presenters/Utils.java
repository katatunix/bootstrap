package com.nghiabui.bootstrap.presentation.presenters;

import java.util.Collection;

class Utils {
	
	public static void printOneLine(Console console, Collection<String> elements) {
		boolean first = true;
		for (String e : elements) {
			if (!first) console.info(", ");
			else first = false;
			console.info(e);
		}

		console.infoln();
	}
	
}
