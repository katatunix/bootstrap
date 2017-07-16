package com.nghiabui.bootstrap.core;

import java.util.Iterator;
import java.util.Set;

public class ParallelMultiLogCombine<Ele, Fri> implements MultiLogCombine<Ele, Fri> {

	private final Set<Log> logs;
	private final SingleCombine<Ele, Fri> singleCombine;

	public ParallelMultiLogCombine(Set<Log> logs, SingleCombine<Ele, Fri> singleCombine) {
		this.logs = logs;
		this.singleCombine = singleCombine;
	}

	@Override
	public FriendMap<Fri> apply(Ele ele) {
		final FriendMap<Fri> fm = new FriendMap<>();
		final int numJobs = Runtime.getRuntime().availableProcessors();
		final Thread[] threads = new Thread[numJobs];
		final Iterator<Log> iter = logs.iterator();
		final Object lock = new Object();

		for (int i = 0; i < threads.length; ++i) {
			threads[i] = new Thread(() -> {
				while (true) {
					final Log log;
					synchronized (lock) {
						log = iter.hasNext() ? iter.next() : null;
					}
					if (log == null) break;
					final Set<Fri> friends = singleCombine.apply(ele, log);
					synchronized (lock) {
						for (Fri friend : friends) {
							fm.add(friend, log);
						}
					}
				}
			});
			threads[i].start();
		}

		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return fm;
	}

}
