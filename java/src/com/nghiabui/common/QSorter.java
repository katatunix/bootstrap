package com.nghiabui.common;

import java.util.List;
import java.util.function.BiPredicate;

public class QSorter {
	
	public static <T> void sort(List<T> list, BiPredicate<T, T> lessFunc) {
		if (list.isEmpty()) return;
		sort(list, lessFunc, 0, list.size() - 1);
	}
	
	private static <T> void sort(List<T> list, BiPredicate<T, T> lessFunc, int low, int high) {
		int i = low, j = high;
		final T pivot = list.get((i + j) / 2);
		
		while (i <= j) {
			while (lessFunc.test(pivot, list.get(i))) ++i;
			while (lessFunc.test(list.get(j), pivot)) --j;
			
			if (i <= j) {
				final T tmp = list.get(i);
				list.set(i, list.get(j));
				list.set(j, tmp);
				++i;
				--j;
			}
		}
		
		if (low < j)
			sort(list, lessFunc, low, j);
		if (i < high)
			sort(list, lessFunc, i, high);
	}

}
