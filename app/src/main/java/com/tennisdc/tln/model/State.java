package com.tennisdc.tln.model;

import java.util.List;

public class State {
	public String code;
	public List<Domain> domains = null;

	@Override
	public String toString() {
		return code;
	}
}

