package com.nghiabui.bootstrap.core;

import java.util.Set;

public class SeqMultiLogCombine<Ele, Fri> implements MultiLogCombine<Ele, Fri> {

	private final Set<Log> logs;
	private final SingleCombine<Ele, Fri> singleCombine;

	public SeqMultiLogCombine(Set<Log> logs, SingleCombine<Ele, Fri> singleCombine) {
		this.logs = logs;
		this.singleCombine = singleCombine;
	}

	@Override
	public FriendMap<Fri> apply(Ele ele) {
		final FriendMap<Fri> fm = new FriendMap<>();
		for (Log log : logs) {
			for (Fri friend : singleCombine.apply(ele, log)) {
				fm.add(friend, log);
			}
		}
		return fm;
	}

}
