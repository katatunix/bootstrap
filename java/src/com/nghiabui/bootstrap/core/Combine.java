package com.nghiabui.bootstrap.core;

import com.nghiabui.common.Ticker;
import com.nghiabui.common.Tuple;

import java.util.HashSet;
import java.util.Set;

public class Combine<Ele, Fri> {

	private final MultiLogCombine<Ele, Fri> origin;

	public Combine(MultiLogCombine<Ele, Fri> origin) {
		this.origin = origin;
	}

	public Set<Fri> apply(Ele e) {
		return origin.apply(e).keys();
	}

	public Set<Fri> apply(Set<Ele> elements) {
		return apply(elements, 0).x;
	}

	public Tuple<Set<Fri>, Set<Ele>> apply(Set<Ele> elements, int timeoutSec) {
		final Ticker ticker = new Ticker();
		final Set<Fri> friends = new HashSet<>();
		final Set<Ele> pending = new HashSet<>(elements);
		for (Ele e : elements) {
			friends.addAll(apply(e));
			pending.remove(e);
			if (timeoutSec > 0 && ticker.duration() > timeoutSec) {
				break;
			}
		}
		return new Tuple<>(friends, pending);
	}

}
