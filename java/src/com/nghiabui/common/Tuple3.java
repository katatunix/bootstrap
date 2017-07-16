package com.nghiabui.common;

public class Tuple3<X, Y, Z> {
	
	public final X x;
	public final Y y;
	public final Z z;

	public Tuple3(X x, Y y, Z z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Tuple3<?, ?, ?> t = (Tuple3<?, ?, ?>) o;
		return x.equals(t.x) && y.equals(t.y) && z.equals(t.z);
	}
	
	@Override
	public int hashCode() {
		int result = x.hashCode();
		result = 31 * result + y.hashCode();
		result = 31 * result + z.hashCode();
		return result;
	}
	
}
