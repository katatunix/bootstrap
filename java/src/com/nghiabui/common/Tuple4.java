package com.nghiabui.common;

public class Tuple4<X, Y, Z, T> {
	
	public final X x;
	public final Y y;
	public final Z z;
	public final T t;

	public Tuple4(X x, Y y, Z z, T t) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.t = t;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tuple4<?, ?, ?, ?> t = (Tuple4<?, ?, ?, ?>) o;
		return x.equals(t.x) && y.equals(t.y) && z.equals(t.z) && this.t.equals(t.t);
	}
	
	@Override
	public int hashCode() {
		int result = x.hashCode();
		result = 31 * result + y.hashCode();
		result = 31 * result + z.hashCode();
		result = 31 * result + t.hashCode();
		return result;
	}
	
}
