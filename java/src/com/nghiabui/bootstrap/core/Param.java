package com.nghiabui.bootstrap.core;

import java.io.Serializable;

public class Param implements Serializable {
	
	public final int iter;
	public final double threshold;
	public final int bestWordNumber;
	public final int timeoutSec;

	public Param(int iter, double threshold, int bestWordNumber, int timeoutSec) {
		this.iter = iter;
		this.threshold = threshold;
		this.bestWordNumber = bestWordNumber;
		this.timeoutSec = timeoutSec;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Param param = (Param) o;

		if (iter != param.iter) return false;
		if (Double.compare(param.threshold, threshold) != 0) return false;
		if (bestWordNumber != param.bestWordNumber) return false;
		return timeoutSec == param.timeoutSec;
	}

	@Override
	public int hashCode() {
		int result;
		long temp;
		result = iter;
		temp = Double.doubleToLongBits(threshold);
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		result = 31 * result + bestWordNumber;
		result = 31 * result + timeoutSec;
		return result;
	}

}
