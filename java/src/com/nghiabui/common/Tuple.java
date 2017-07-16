package com.nghiabui.common;

public class Tuple<X, Y> {
	
	public final X x;
	public final Y y;

	public Tuple(X x, Y y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tuple<?, ?> t = (Tuple<?, ?>) o;
		return x.equals(t.x) && y.equals(t.y);
	}
	
	@Override
	public int hashCode() {
		int result = x.hashCode();
		result = 31 * result + y.hashCode();
		return result;
	}
	
}
