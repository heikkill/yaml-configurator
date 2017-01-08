package com.heikkill.yaml;


public interface Provider<T> {

	public T get(String name);
	
}
