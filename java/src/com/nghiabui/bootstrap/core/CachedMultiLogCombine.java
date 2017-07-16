package com.nghiabui.bootstrap.core;

public class CachedMultiLogCombine<Ele, Fri> implements MultiLogCombine<Ele, Fri> {

	private final MultiLogCombine<Ele, Fri> origin;
	private final RelationCache<Ele, Fri> cache;

	public CachedMultiLogCombine(MultiLogCombine<Ele, Fri> origin, RelationCache<Ele, Fri> cache) {
		this.origin = origin;
		this.cache = cache;
	}

	@Override
	public FriendMap<Fri> apply(Ele ele) {
		FriendMap<Fri> fm = cache.get(ele);
		if (fm == null) {
			fm = origin.apply(ele);
			cache.add(ele, fm);
		}
		return fm;
	}

}
