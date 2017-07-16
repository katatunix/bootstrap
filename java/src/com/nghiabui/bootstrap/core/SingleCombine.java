package com.nghiabui.bootstrap.core;

import java.util.Set;

public interface SingleCombine<Ele, Fri> {

	Set<Fri> apply(Ele ele, Log log);

}
